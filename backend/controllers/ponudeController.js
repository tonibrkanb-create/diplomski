const ponudeService = require('../services/ponudeService');

const list = async (req, res) => {
  try {
    const ponude = await ponudeService.getByKorisnik(req.korisnik.korisnikId);
    res.json(ponude);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const getById = async (req, res) => {
  try {
    const ponuda = await ponudeService.getById(req.params.id, req.korisnik.korisnikId);
    res.json(ponuda);
  } catch (error) {
    res.status(404).json({ message: error.message });
  }
};

const create = async (req, res) => {
  try {
    const ponuda = await ponudeService.create(req.korisnik.korisnikId, req.body);
    res.status(201).json(ponuda);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

module.exports = { list, getById, create };
