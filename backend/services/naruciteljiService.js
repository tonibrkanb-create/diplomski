const db = require('../models');

class NaruciteljiService {
  parseNumber(value, fallback) {
    const parsed = parseInt(value, 10);
    return Number.isNaN(parsed) ? fallback : parsed;
  }

  buildSearchWhere(searchTerm, filters = {}) {
    const where = {};
    const { Op } = db.Sequelize;

    if (searchTerm) {
      where[Op.or] = [
        { name: { [Op.like]: `%${searchTerm}%` } },
        { mjesto: { [Op.like]: `%${searchTerm}%` } },
        { adresa: { [Op.like]: `%${searchTerm}%` } },
        { email: { [Op.like]: `%${searchTerm}%` } },
        { OIB: { [Op.like]: `%${searchTerm}%` } }
      ];
    }

    const filterableFields = ['name', 'mjesto', 'drzava', 'postanskiBroj', 'OIB', 'email'];
    for (const field of filterableFields) {
      const value = filters[field];
      if (typeof value === 'string' && value.trim().length > 0) {
        where[field] = { [Op.like]: `%${value.trim()}%` };
      }
    }

    return where;
  }

  resolveSorting(sortBy, sortOrder) {
    const allowedSortFields = [
      'id',
      'name',
      'mjesto',
      'drzava',
      'postanskiBroj',
      'OIB',
      'email',
      'createdAt',
      'updatedAt'
    ];

    const finalSortBy = allowedSortFields.includes(sortBy) ? sortBy : 'name';
    const normalizedSortOrder = String(sortOrder || 'ASC').toUpperCase();
    const finalSortOrder = normalizedSortOrder === 'DESC' ? 'DESC' : 'ASC';

    return [finalSortBy, finalSortOrder];
  }

  async getAllNarucitelji(options = {}) {
    try {
      const searchTerm = options.search || '';
      const shouldPaginate = options.page !== undefined || options.pageSize !== undefined;
      const page = Math.max(1, this.parseNumber(options.page, 1));
      const pageSize = shouldPaginate
        ? Math.min(100, Math.max(1, this.parseNumber(options.pageSize, 10)))
        : null;
      const offset = shouldPaginate ? (page - 1) * pageSize : undefined;
      const order = [this.resolveSorting(options.sortBy, options.sortOrder)];
      const where = this.buildSearchWhere(searchTerm, options.filters);

      if (!shouldPaginate) {
        const rows = await db.narucitelj.findAll({
          where,
          order,
          include: [{
            model: db.radni_nalog,
            as: 'radniNalozi',
            required: false
          }]
        });

        return {
          items: rows,
          page: 1,
          pageSize: rows.length,
          totalItems: rows.length,
          totalPages: 1,
          sortBy: order[0][0],
          sortOrder: order[0][1]
        };
      }

      const result = await db.narucitelj.findAndCountAll({
        where,
        limit: pageSize,
        offset,
        order,
        distinct: true,
        include: [{
          model: db.radni_nalog,
          as: 'radniNalozi',
          required: false
        }]
      });

      const totalItems = result.count;
      const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));

      return {
        items: result.rows,
        page,
        pageSize,
        totalItems,
        totalPages,
        sortBy: order[0][0],
        sortOrder: order[0][1]
      };
    } catch (error) {
      throw new Error(`Error fetching narucitelji: ${error.message}`);
    }
  }

  async getNaruciteljiById(id) {
    try {
      const narucitelj = await db.narucitelj.findByPk(id, {
        include: [{
          model: db.radni_nalog,
          as: 'radniNalozi',
          required: false
        }]
      });

      if (!narucitelj) {
        throw new Error('Narucitelj not found');
      }

      return narucitelj;
    } catch (error) {
      throw new Error(`Error fetching narucitelj: ${error.message}`);
    }
  }

  async createNarucitelj(data) {
    try {
      const nameValue = data.narucitelj || data.name;
      return await db.narucitelj.create({
        name: nameValue,
        adresa: data.adresa || null,
        mjesto: data.mjesto || null,
        postanskiBroj: data.postanskiBroj || null,
        drzava: data.drzava || null,
        OIB: data.OIB || null,
        ziroRacun: data.ziroRacun || null,
        ostalo: data.ostalo || null,
        kontaktOsoba: data.kontaktOsoba || null,
        telefon: data.telefon || null,
        mobitel: data.mobitel || null,
        fax: data.fax || null,
        email: data.email || null,
        location: data.mjesto,
        comment: data.comment || null
      });
    } catch (error) {
      throw new Error(`Error creating narucitelj: ${error.message}`);
    }
  }

  async updateNarucitelj(id, data) {
    try {
      const narucitelj = await db.narucitelj.findByPk(id);

      if (!narucitelj) {
        throw new Error('Narucitelj not found');
      }

      const nameValue = data.narucitelj !== undefined ? data.narucitelj : data.name;
      return await narucitelj.update({
        name: nameValue || narucitelj.name,
        adresa: data.adresa !== undefined ? data.adresa : narucitelj.adresa,
        mjesto: data.mjesto !== undefined ? data.mjesto : narucitelj.mjesto,
        postanskiBroj: data.postanskiBroj !== undefined ? data.postanskiBroj : narucitelj.postanskiBroj,
        drzava: data.drzava !== undefined ? data.drzava : narucitelj.drzava,
        OIB: data.OIB !== undefined ? data.OIB : narucitelj.OIB,
        ziroRacun: data.ziroRacun !== undefined ? data.ziroRacun : narucitelj.ziroRacun,
        ostalo: data.ostalo !== undefined ? data.ostalo : narucitelj.ostalo,
        kontaktOsoba: data.kontaktOsoba !== undefined ? data.kontaktOsoba : narucitelj.kontaktOsoba,
        telefon: data.telefon !== undefined ? data.telefon : narucitelj.telefon,
        mobitel: data.mobitel !== undefined ? data.mobitel : narucitelj.mobitel,
        fax: data.fax !== undefined ? data.fax : narucitelj.fax,
        email: data.email !== undefined ? data.email : narucitelj.email,
        location: data.location || narucitelj.location,
        comment: data.comment !== undefined ? data.comment : narucitelj.comment
      });
    } catch (error) {
      throw new Error(`Error updating narucitelj: ${error.message}`);
    }
  }

  async deleteNarucitelj(id) {
    try {
      const narucitelj = await db.narucitelj.findByPk(id);

      if (!narucitelj) {
        throw new Error('Narucitelj not found');
      }

      await narucitelj.destroy();
      return narucitelj;
    } catch (error) {
      throw new Error(`Error deleting narucitelj: ${error.message}`);
    }
  }
}

module.exports = new NaruciteljiService();
