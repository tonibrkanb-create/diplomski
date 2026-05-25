const obavijestiManagementService = require('../services/obavijestiManagementService');

const create = async (req, res) => {
  try {
    const { korisnikId, naslov, poruka } = req.body;
    const obavijest = await obavijestiManagementService.create(korisnikId, naslov, poruka);
    res.status(201).json(obavijest);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

const getKorisnici = async (req, res) => {
  try {
    const korisnici = await obavijestiManagementService.getAllKorisnici();
    res.json(korisnici);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

module.exports = { create, getKorisnici };
