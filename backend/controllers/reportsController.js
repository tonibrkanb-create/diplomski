const reportsService = require('../services/reportsService');

const getNaloziReport = async (req, res) => {
  try {
    const data = await reportsService.getNaloziReport(req.query);
    res.json(data);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const getNaruciteljiReport = async (req, res) => {
  try {
    const data = await reportsService.getNaruciteljiReport();
    res.json(data);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const getMyTasks = async (req, res) => {
  try {
    const data = await reportsService.getMyTasks(req.user.id);
    res.json(data);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

module.exports = { getNaloziReport, getNaruciteljiReport, getMyTasks };
