const db = require('../models');
const Obavijest = db.obavijest;
const Korisnik = db.korisnik;

const create = async (korisnikId, naslov, poruka) => {
  if (!korisnikId || !naslov || !poruka) {
    throw new Error('Korisnik, naslov i poruka su obavezni');
  }

  const korisnik = await Korisnik.findByPk(korisnikId);
  if (!korisnik) throw new Error('Korisnik nije pronađen');

  return Obavijest.create({ korisnikId, naslov, poruka });
};

const getAllKorisnici = async () => {
  return Korisnik.findAll({
    where: { isActive: true },
    attributes: ['id', 'ime', 'prezime', 'email', 'tvrtka'],
    order: [['ime', 'ASC']]
  });
};

module.exports = { create, getAllKorisnici };
