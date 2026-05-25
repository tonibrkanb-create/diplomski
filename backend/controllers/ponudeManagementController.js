const ponudeManagementService = require('../services/ponudeManagementService');

const listAll = async (req, res) => {
  try {
    const ponude = await ponudeManagementService.getAll();
    res.json(ponude);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const getById = async (req, res) => {
  try {
    const ponuda = await ponudeManagementService.getById(req.params.id);
    res.json(ponuda);
  } catch (error) {
    res.status(404).json({ message: error.message });
  }
};

const updateStatus = async (req, res) => {
  try {
    const { status, odgovor } = req.body;
    const ponuda = await ponudeManagementService.updateStatus(req.params.id, status, odgovor);
    res.json(ponuda);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

module.exports = { listAll, getById, updateStatus };
