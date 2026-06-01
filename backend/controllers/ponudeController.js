const ponudeService = require('../services/ponudeService');
const logService = require('../services/logService');

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

const updateStatus = async (req, res) => {
  try {
    const { status } = req.body;
    const ponuda = await ponudeService.updateStatusByKorisnik(req.params.id, req.korisnik.korisnikId, status);
    
    // Log the status update
    await logService.log(
      'UPDATE_STATUS',
      'ponuda',
      ponuda.id,
      null,
      `Korisnik ${req.korisnik.korisnikId} je ${status === 'odobrena' ? 'prihvatio' : 'odbio'} ponudu`
    );
    
    res.json(ponuda);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

module.exports = { list, getById, create, updateStatus };
