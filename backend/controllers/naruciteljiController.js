const naruciteljiService = require('../services/naruciteljiService');

class NaruciteljiController {
  async getAll(req, res) {
    try {
      const search = req.query.q || req.query.search || req.query.narucitelj || '';
      const hasGridParams = [
        'page',
        'pageSize',
        'sortBy',
        'sortOrder',
        'name',
        'mjesto',
        'drzava',
        'postanskiBroj',
        'OIB',
        'email'
      ].some((key) => req.query[key] !== undefined);

      const narucitelji = await naruciteljiService.getAllNarucitelji({
        search,
        page: req.query.page,
        pageSize: req.query.pageSize,
        sortBy: req.query.sortBy,
        sortOrder: req.query.sortOrder,
        filters: {
          name: req.query.name,
          mjesto: req.query.mjesto,
          drzava: req.query.drzava,
          postanskiBroj: req.query.postanskiBroj,
          OIB: req.query.OIB,
          email: req.query.email
        }
      });

      if (!hasGridParams) {
        res.json(narucitelji.items);
        return;
      }

      res.json(narucitelji);
    } catch (error) {
      res.status(500).json({ message: error.message });
    }
  }

  async getById(req, res) {
    try {
      const narucitelj = await naruciteljiService.getNaruciteljiById(parseInt(req.params.id));
      res.json(narucitelj);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }

  async create(req, res) {
    try {
      const narucitelj = await naruciteljiService.createNarucitelj(req.body);
      res.status(201).json(narucitelj);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  }

  async update(req, res) {
    try {
      const narucitelj = await naruciteljiService.updateNarucitelj(parseInt(req.params.id), req.body);
      res.json(narucitelj);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  }

  async delete(req, res) {
    try {
      const narucitelj = await naruciteljiService.deleteNarucitelj(parseInt(req.params.id));
      res.json(narucitelj);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }
}

module.exports = new NaruciteljiController();
