const logService = require('../services/logService');

const list = async (req, res) => {
  try {
    const { entity, action, from, to } = req.query;
    const logs = await logService.getAll({ entity, action, from, to });
    res.json(logs);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

module.exports = { list };
