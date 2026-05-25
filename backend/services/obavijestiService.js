const db = require('../models');
const Obavijest = db.obavijest;

const getByKorisnik = async (korisnikId) => {
  return Obavijest.findAll({
    where: { korisnikId },
    order: [['createdAt', 'DESC']]
  });
};

const markAsRead = async (id, korisnikId) => {
  const obavijest = await Obavijest.findOne({ where: { id, korisnikId } });
  if (!obavijest) {
    throw new Error('Obavijest nije pronađena');
  }

  obavijest.procitana = true;
  await obavijest.save();
  return obavijest;
};

module.exports = { getByKorisnik, markAsRead };
