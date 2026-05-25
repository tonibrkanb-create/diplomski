const db = require('../models');
const Recenzija = db.recenzija;

const getByKorisnik = async (korisnikId) => {
  return Recenzija.findAll({
    where: { korisnikId },
    order: [['createdAt', 'DESC']]
  });
};

const getById = async (id, korisnikId) => {
  const recenzija = await Recenzija.findOne({ where: { id, korisnikId } });
  if (!recenzija) {
    throw new Error('Recenzija nije pronađena');
  }
  return recenzija;
};

const create = async (korisnikId, { radniNalogId, ocjena, komentar }) => {
  if (!ocjena || ocjena < 1 || ocjena > 5) {
    throw new Error('Ocjena mora biti između 1 i 5');
  }

  return Recenzija.create({
    korisnikId,
    radniNalogId: radniNalogId || null,
    ocjena,
    komentar: komentar || null
  });
};

module.exports = { getByKorisnik, getById, create };
