const db = require('../models');

class AktivnostiService {
  async getAllAktivnosti() {
    try {
      return await db.aktivnost.findAll({
        where: { isActive: true },
        order: [['id', 'ASC']]
      });
    } catch (error) {
      throw new Error(`Error fetching aktivnosti: ${error.message}`);
    }
  }

  async getAktivnostById(id) {
    try {
      const aktivnost = await db.aktivnost.findByPk(id);

      if (!aktivnost || !aktivnost.isActive) {
        throw new Error('Aktivnost not found');
      }

      return aktivnost;
    } catch (error) {
      throw new Error(`Error fetching aktivnost: ${error.message}`);
    }
  }

  async createAktivnost(data) {
    try {
      return await db.aktivnost.create({
        aktivnost: data.aktivnost,
        rokTrajanja: data.rokTrajanja,
        cijena: data.cijena !== undefined ? data.cijena : null,
        isActive: true
      });
    } catch (error) {
      throw new Error(`Error creating aktivnost: ${error.message}`);
    }
  }

  async updateAktivnost(id, data) {
    try {
      const aktivnost = await db.aktivnost.findByPk(id);

      if (!aktivnost || !aktivnost.isActive) {
        throw new Error('Aktivnost not found');
      }

      return await aktivnost.update({
        aktivnost: data.aktivnost !== undefined ? data.aktivnost : aktivnost.aktivnost,
        rokTrajanja: data.rokTrajanja !== undefined ? data.rokTrajanja : aktivnost.rokTrajanja,
        cijena: data.cijena !== undefined ? data.cijena : aktivnost.cijena
      });
    } catch (error) {
      throw new Error(`Error updating aktivnost: ${error.message}`);
    }
  }

  async deleteAktivnost(id) {
    try {
      const aktivnost = await db.aktivnost.findByPk(id);

      if (!aktivnost || !aktivnost.isActive) {
        throw new Error('Aktivnost not found');
      }

      return await aktivnost.update({ isActive: false });
    } catch (error) {
      throw new Error(`Error deleting aktivnost: ${error.message}`);
    }
  }
}

module.exports = new AktivnostiService();