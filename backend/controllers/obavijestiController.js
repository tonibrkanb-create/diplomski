const obavijestiService = require('../services/obavijestiService');

const list = async (req, res) => {
  try {
    const obavijesti = await obavijestiService.getByKorisnik(req.korisnik.korisnikId);
    res.json(obavijesti);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const markAsRead = async (req, res) => {
  try {
    const obavijest = await obavijestiService.markAsRead(req.params.id, req.korisnik.korisnikId);
    res.json(obavijest);
  } catch (error) {
    res.status(404).json({ message: error.message });
  }
};

module.exports = { list, markAsRead };
