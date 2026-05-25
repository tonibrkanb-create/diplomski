const statisticsService = require('../services/statisticsService');

const getDashboard = async (req, res) => {
  try {
    const stats = await statisticsService.getDashboardStats();
    res.json(stats);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const getRevenueByAktivnost = async (req, res) => {
  try {
    const data = await statisticsService.getRevenueByAktivnost();
    res.json(data);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const getPerformanceByWorker = async (req, res) => {
  try {
    const data = await statisticsService.getPerformanceByWorker();
    res.json(data);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const getIssuedByMonth = async (req, res) => {
  try {
    const data = await statisticsService.getIssuedByMonth();
    res.json(data);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

module.exports = { getDashboard, getRevenueByAktivnost, getPerformanceByWorker, getIssuedByMonth };
