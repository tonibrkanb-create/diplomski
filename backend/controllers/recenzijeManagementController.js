const recenzijeManagementService = require('../services/recenzijeManagementService');

const listAll = async (req, res) => {
  try {
    const recenzije = await recenzijeManagementService.getAll();
    res.json(recenzije);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const respond = async (req, res) => {
  try {
    const { odgovor } = req.body;
    const recenzija = await recenzijeManagementService.respond(req.params.id, odgovor);
    res.json(recenzija);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

module.exports = { listAll, respond };
