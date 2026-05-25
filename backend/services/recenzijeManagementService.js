const db = require('../models');
const Recenzija = db.recenzija;
const Korisnik = db.korisnik;

const getAll = async () => {
  return Recenzija.findAll({
    include: [{
      model: Korisnik,
      attributes: ['id', 'ime', 'prezime', 'email']
    }],
    order: [['createdAt', 'DESC']]
  });
};

const respond = async (id, odgovor) => {
  const recenzija = await Recenzija.findByPk(id);
  if (!recenzija) throw new Error('Recenzija nije pronađena');

  recenzija.odgovor = odgovor;
  await recenzija.save();
  return recenzija;
};

module.exports = { getAll, respond };
