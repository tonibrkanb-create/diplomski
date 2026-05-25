const aktivnostiService = require('../services/aktivnostiService');

class AktivnostiController {
  async getAll(req, res) {
    try {
      const aktivnosti = await aktivnostiService.getAllAktivnosti();
      res.json(aktivnosti);
    } catch (error) {
      res.status(500).json({ message: error.message });
    }
  }

  async getById(req, res) {
    try {
      const aktivnost = await aktivnostiService.getAktivnostById(parseInt(req.params.id));
      res.json(aktivnost);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }

  async create(req, res) {
    try {
      const aktivnost = await aktivnostiService.createAktivnost(req.body);
      res.status(201).json(aktivnost);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  }

  async update(req, res) {
    try {
      const aktivnost = await aktivnostiService.updateAktivnost(parseInt(req.params.id), req.body);
      res.json(aktivnost);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  }

  async delete(req, res) {
    try {
      const aktivnost = await aktivnostiService.deleteAktivnost(parseInt(req.params.id));
      res.json(aktivnost);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }
}

module.exports = new AktivnostiController();