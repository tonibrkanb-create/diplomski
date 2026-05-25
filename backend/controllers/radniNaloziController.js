const radniNaloziService = require('../services/radniNaloziService');
const radniNalogPdfService = require('../services/radniNalogPdfService');

class RadniNaloziController {
    async getNextBrojNaloga(req, res) {
      try {
        const nextBroj = await radniNaloziService.getNextBrojNaloga();
        res.json({ brojNaloga: nextBroj });
      } catch (error) {
        res.status(500).json({ message: error.message });
      }
    }
  async getUskoroIstice(req, res) {
    try {
      const parsedDays = parseInt(req.query.days, 10);
      const days = Number.isNaN(parsedDays) ? 1000 : parsedDays;
      const uskoroIstice = await radniNaloziService.getUskoroIstice(days);
      const response = uskoroIstice.map((item) => ({
        ...item,
        isActive: item.isActive !== undefined ? item.isActive : true
      }));
      res.json(response);
    } catch (error) {
      res.status(500).json({ message: error.message });
    }
  }

  async getAll(req, res) {
    try {
      const nalozi = await radniNaloziService.getAllRadniNalozi();
      res.json(nalozi);
    } catch (error) {
      res.status(500).json({ message: error.message });
    }
  }

  async getById(req, res) {
    try {
      const nalog = await radniNaloziService.getRadniNalogById(parseInt(req.params.id));
      res.json(nalog);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }

  async getByNarucitelj(req, res) {
    try {
      const nalozi = await radniNaloziService.getRadniNaloziByNarucitelj(parseInt(req.params.naruciteljiId));
      res.json(nalozi);
    } catch (error) {
      res.status(500).json({ message: error.message });
    }
  }

  async create(req, res) {
    try {
      console.log('Creating Radni Nalog with data:', req.body);
      const nalog = await radniNaloziService.createRadniNalog(req.body);
      res.status(201).json(nalog);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  }

  async update(req, res) {
    try {
      const nalog = await radniNaloziService.updateRadniNalog(parseInt(req.params.id), req.body);
      res.json(nalog);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  }

  async assignWorker(req, res) {
    try {
      const { assignedUserId } = req.body;
      const nalog = await radniNaloziService.assignWorker(parseInt(req.params.id), assignedUserId);
      res.json(nalog);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  }

  async delete(req, res) {
    try {
      const nalog = await radniNaloziService.deleteRadniNalog(parseInt(req.params.id));
      res.json(nalog);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }

  async downloadPdf(req, res) {
    try {
      const result = await radniNalogPdfService.generateRadniNalogPdf(parseInt(req.params.id));
      res.setHeader('Content-Type', result.contentType);
      res.setHeader('Content-Disposition', `attachment; filename="${result.fileName}"`);
      res.send(result.buffer);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }

  async downloadRn001Pdf(req, res) {
    try {
      const result = await radniNalogPdfService.generateRadniNalogPdf(1);
      res.setHeader('Content-Type', result.contentType);
      res.setHeader('Content-Disposition', 'attachment; filename="RN001.pdf"');
      res.send(result.buffer);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }
}

module.exports = new RadniNaloziController();
