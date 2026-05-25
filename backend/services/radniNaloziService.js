const db = require('../models');

class RadniNaloziService {
  constructor() {
    this._aktivnostiSupportsArrayCache = null;
  }

  formatDateOnly(dateValue) {
    const date = new Date(dateValue);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  calculateDatumIsteka(datum, trajanjeMjeseci) {
    const datumIstekaDate = new Date(datum);
    datumIstekaDate.setMonth(datumIstekaDate.getMonth() + trajanjeMjeseci);
    return datumIstekaDate;
  }

  normalizeAktivnostiInput(aktivnostiInput) {
    if (aktivnostiInput === null || aktivnostiInput === undefined) {
      return [];
    }

    if (!Array.isArray(aktivnostiInput)) {
      throw new Error('Aktivnosti must be an array');
    }

    const sanitized = [];

    for (const item of aktivnostiInput) {
      let parsedId = null;

      if (typeof item === 'number' && Number.isInteger(item) && item > 0) {
        parsedId = item;
      } else if (typeof item === 'string') {
        const trimmed = item.trim();
        if (/^\d+$/.test(trimmed)) {
          const numericValue = Number(trimmed);
          if (Number.isInteger(numericValue) && numericValue > 0) {
            parsedId = numericValue;
          }
        }
      }

      if (!parsedId) {
        throw new Error('Aktivnosti must be an array of numeric IDs');
      }

      if (!sanitized.includes(parsedId)) {
        sanitized.push(parsedId);
      }
    }

    return sanitized;
  }

  async supportsArrayAktivnostiStorage() {
    if (this._aktivnostiSupportsArrayCache !== null) {
      return this._aktivnostiSupportsArrayCache;
    }

    const tableDefinition = await db.sequelize.getQueryInterface().describeTable('radni_nalozi');
    const aktivnostiColumn = tableDefinition.aktivnosti;
    const columnType = aktivnostiColumn && typeof aktivnostiColumn.type === 'string'
      ? aktivnostiColumn.type.toUpperCase()
      : '';

    this._aktivnostiSupportsArrayCache = columnType.includes('TEXT');
    return this._aktivnostiSupportsArrayCache;
  }

  serializeAktivnostiForStorage(aktivnostiList, supportsArrayStorage) {
    if (supportsArrayStorage) {
      return JSON.stringify(aktivnostiList);
    }

    return aktivnostiList.length > 0 ? aktivnostiList[0] : null;
  }

  async replaceUskoroIsticeEntries(nalog, aktivnostiList, transaction) {
    await db.uskoro_istice.destroy({
      where: { radni_nalog_id: nalog.id },
      transaction
    });

    if (aktivnostiList.length === 0) {
      return;
    }

    const aktivnostiConfig = await db.aktivnost.findAll({
      where: {
        id: {
          [db.Sequelize.Op.in]: aktivnostiList
        },
        isActive: true
      },
      transaction
    });

    const configById = aktivnostiConfig.reduce((accumulator, item) => {
      accumulator[item.id] = {
        aktivnost: item.aktivnost,
        rokTrajanja: item.rokTrajanja
      };
      return accumulator;
    }, {});

    const missingIds = aktivnostiList.filter((id) => !configById[id]);
    if (missingIds.length > 0) {
      throw new Error(`Aktivnosti IDs are invalid or inactive: ${missingIds.join(', ')}`);
    }

    for (const aktivnostId of aktivnostiList) {
      const config = configById[aktivnostId];
      const rokTrajanjaMjeseci = config.rokTrajanja;

      const datumIsteka = this.calculateDatumIsteka(nalog.datum, rokTrajanjaMjeseci);

      if(rokTrajanjaMjeseci <= 0){
        continue; // skip creating uskoro istice entry for non-positive durations
      }
      await db.uskoro_istice.create({
        narucitelj_id: nalog.narucitelj_id,
        radni_nalog_id: nalog.id,
        aktivnost: config.aktivnost,
        datumIsteka: this.formatDateOnly(datumIsteka),
        isActive: true
      }, { transaction });
    }
  }

  async getUskoroIstice(days = 30) {
    try {
      const now = new Date();
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      const threshold = new Date(today);
      threshold.setDate(threshold.getDate() + days);

      const items = await db.uskoro_istice.findAll({
        where: {
          isActive: true,
          datumIsteka: {
            [db.Sequelize.Op.between]: [this.formatDateOnly(today), this.formatDateOnly(threshold)]
          }
        },
        include: [
          {
            model: db.narucitelj,
            as: 'narucitelj'
          },
          {
            model: db.radni_nalog,
            as: 'radniNalog'
          }
        ],
        order: [['datumIsteka', 'ASC']]
      });

      const results = items.map((item) => ({
        id: item.id,
        narucitelj: item.narucitelj ? item.narucitelj.name : null,
        radniNalog: item.radniNalog ? item.radniNalog.brojNaloga : null,
        aktivnost: item.aktivnost,
        datumIsteka: item.datumIsteka,
        isActive: item.isActive
      }));

      return results;
    } catch (error) {
      throw new Error(`Error fetching uskoro istice: ${error.message}`);
    }
  }

  async assignWorker(nalogId, userId) {
    const nalog = await db.radni_nalog.findByPk(nalogId);
    if (!nalog) throw new Error('Radni nalog not found');

    if (userId) {
      const user = await db.user.findByPk(userId);
      if (!user) throw new Error('User not found');
    }

    nalog.assignedUserId = userId || null;
    await nalog.save();
    return nalog;
  }

  async getAllRadniNalozi() {
    try {
      return await db.radni_nalog.findAll({
        include: [
          {
            model: db.narucitelj,
            as: 'narucitelj'
          },
          {
            model: db.document,
            as: 'documents',
            required: false
          },
          {
            model: db.note,
            as: 'notes',
            required: false
          },
          {
            model: db.user,
            as: 'assignedUser',
            attributes: ['id', 'username', 'ime', 'prezime'],
            required: false
          }
        ]
      });
    } catch (error) {
      throw new Error(`Error fetching radni nalozi: ${error.message}`);
    }
  }

  async getRadniNalogById(id) {
    try {
      const nalog = await db.radni_nalog.findByPk(id, {
        include: [
          {
            model: db.narucitelj,
            as: 'narucitelj'
          },
          {
            model: db.document,
            as: 'documents',
            required: false
          },
          {
            model: db.note,
            as: 'notes',
            required: false
          },
          {
            model: db.user,
            as: 'assignedUser',
            attributes: ['id', 'username', 'ime', 'prezime'],
            required: false
          }
        ]
      });

      if (!nalog) {
        throw new Error('Radni nalog not found');
      }

      return nalog;
    } catch (error) {
      throw new Error(`Error fetching radni nalog: ${error.message}`);
    }
  }

  async getRadniNaloziByNarucitelj(naruciteljiId) {
    try {
      const nalozi = await db.radni_nalog.findAll({
        where: {
          narucitelj_id: naruciteljiId
        },
        include: [
          {
            model: db.narucitelj,
            as: 'narucitelj'
          },
          {
            model: db.document,
            as: 'documents',
            required: false
          },
          {
            model: db.note,
            as: 'notes',
            required: false
          }
        ]
      });

      return nalozi;
    } catch (error) {
      throw new Error(`Error fetching radni nalozi: ${error.message}`);
    }
  }

  async createRadniNalog(data) {
    try {
      if (!Object.prototype.hasOwnProperty.call(data, 'aktivnosti')) {
        throw new Error('Aktivnosti field is required and must be an array');
      }

      const aktivnostiList = this.normalizeAktivnostiInput(data.aktivnosti);
      const supportsArrayStorage = await this.supportsArrayAktivnostiStorage();

      if (!supportsArrayStorage && aktivnostiList.length > 1) {
        throw new Error('Database schema supports only one aktivnost per radni nalog. Run latest migrations to enable multiple aktivnosti.');
      }

      // ensure narucitelj exists before creating
      const klijent = await db.narucitelj.findByPk(data.naruciteljId);
      if (!klijent) {
        throw new Error('Narucitelj not found');
      }
      var nextNumber = data.brojNaloga || null;
      if(nextNumber == null){
        nextNumber = await this.getNextBrojNaloga();
      }
      // Enforce RN + 3 digit format
      if (!/^RN\d{3}$/.test(nextNumber)) {
        throw new Error('Broj naloga must be in format RN followed by 3 digits, e.g. RN001');
      }

      return await db.sequelize.transaction(async (transaction) => {
        const nalog = await db.radni_nalog.create({
          brojNaloga: nextNumber,
          narucitelj_id: data.naruciteljId,
          datum: new Date(data.datum),
          objekt: data.objekt,
          fakturirano: data.fakturirano || false,
          zavrseno: data.zavrseno || false,
          opis: data.opis || null,
          brojPonude: data.brojPonude || null,
          brojRacuna: data.brojRacuna || null,
          narudzbenica: data.narudzbenica || null,
          ugovor: data.ugovor || null,
          aktivnosti: this.serializeAktivnostiForStorage(aktivnostiList, supportsArrayStorage),
          pdfUrl: data.pdfUrl || null
        }, { transaction });

        await this.replaceUskoroIsticeEntries(nalog, aktivnostiList, transaction);
        return nalog;
      });
    } catch (error) {
      throw new Error(`Error creating radni nalog: ${error.message}`);
    }
  }

  async updateRadniNalog(id, data) {
    try {
      const nalog = await db.radni_nalog.findByPk(id);

      if (!nalog) {
        throw new Error('Radni nalog not found');
      }

      if (!Object.prototype.hasOwnProperty.call(data, 'aktivnosti')) {
        throw new Error('Aktivnosti field is required and must be an array');
      }

      const nextNaruciteljId = data.narucitelj_id !== undefined
        ? data.narucitelj_id
        : (data.naruciteljId !== undefined ? data.naruciteljId : nalog.narucitelj_id);

      const nextAktivnosti = this.normalizeAktivnostiInput(data.aktivnosti);
      const supportsArrayStorage = await this.supportsArrayAktivnostiStorage();

      if (!supportsArrayStorage && nextAktivnosti.length > 1) {
        throw new Error('Database schema supports only one aktivnost per radni nalog. Run latest migrations to enable multiple aktivnosti.');
      }

      // Enforce RN + 3 digit format if brojNaloga is being updated
      let brojNalogaToCheck = data.brojNaloga !== undefined ? data.brojNaloga : nalog.brojNaloga;
      if (!/^RN\d{3}$/.test(brojNalogaToCheck)) {
        throw new Error('Broj naloga must be in format RN followed by 3 digits, e.g. RN001');
      }

      if (nextNaruciteljId !== undefined) {
        const klijent = await db.narucitelj.findByPk(nextNaruciteljId);
        if (!klijent) {
          throw new Error('Narucitelj not found');
        }
      }

      return await db.sequelize.transaction(async (transaction) => {
        const updatedNalog = await nalog.update({
          narucitelj_id: nextNaruciteljId,
          datum: data.datum ? new Date(data.datum) : nalog.datum,
          objekt: data.objekt || nalog.objekt,
          fakturirano: data.fakturirano !== undefined ? data.fakturirano : nalog.fakturirano,
          zavrseno: data.zavrseno !== undefined ? data.zavrseno : nalog.zavrseno,
          opis: data.opis !== undefined ? data.opis : nalog.opis,
          brojPonude: data.brojPonude !== undefined ? data.brojPonude : nalog.brojPonude,
          brojRacuna: data.brojRacuna !== undefined ? data.brojRacuna : nalog.brojRacuna,
          narudzbenica: data.narudzbenica !== undefined ? data.narudzbenica : nalog.narudzbenica,
          ugovor: data.ugovor !== undefined ? data.ugovor : nalog.ugovor,
          aktivnosti: this.serializeAktivnostiForStorage(nextAktivnosti, supportsArrayStorage),
          pdfUrl: data.pdfUrl !== undefined ? data.pdfUrl : nalog.pdfUrl,
          brojNaloga: brojNalogaToCheck
        }, { transaction });

        await this.replaceUskoroIsticeEntries(updatedNalog, nextAktivnosti, transaction);

        return updatedNalog;
      });
    } catch (error) {
      throw new Error(`Error updating radni nalog: ${error.message}`);
    }
  }

  async deleteRadniNalog(id) {
    try {
      const nalog = await db.radni_nalog.findByPk(id);

      if (!nalog) {
        throw new Error('Radni nalog not found');
      }
      // Validate brojNaloga format before delete
      if (!/^RN\d{3}$/.test(nalog.brojNaloga)) {
        throw new Error('Broj naloga must be in format RN followed by 3 digits, e.g. RN001');
      }

      await nalog.destroy();
      return nalog;
    } catch (error) {
      throw new Error(`Error deleting radni nalog: ${error.message}`);
    }
  }

  async getNextBrojNaloga() {
    try {
      let sequenceNumber = (await db.radni_nalog.count()) + 1;

      while (true) {
        const candidate = `RN${String(sequenceNumber).padStart(3, '0')}`;
        const existing = await db.radni_nalog.findOne({
          where: { brojNaloga: candidate },
          attributes: ['id']
        });

        if (!existing) {
          return candidate;
        }

        sequenceNumber += 1;
      }
    } catch (error) {
      throw new Error(`Error generating broj naloga: ${error.message}`);
    }
  }
}

module.exports = new RadniNaloziService();
