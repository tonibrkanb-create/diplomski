const recenzijeService = require('../services/recenzijeService');

const list = async (req, res) => {
  try {
    const recenzije = await recenzijeService.getByKorisnik(req.korisnik.korisnikId);
    res.json(recenzije);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const getById = async (req, res) => {
  try {
    const recenzija = await recenzijeService.getById(req.params.id, req.korisnik.korisnikId);
    res.json(recenzija);
  } catch (error) {
    res.status(404).json({ message: error.message });
  }
};

const create = async (req, res) => {
  try {
    const recenzija = await recenzijeService.create(req.korisnik.korisnikId, req.body);
    res.status(201).json(recenzija);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

module.exports = { list, getById, create };
