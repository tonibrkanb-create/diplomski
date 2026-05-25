const db = require('../models');
const Ponuda = db.ponuda;
const Korisnik = db.korisnik;

const getAll = async () => {
  return Ponuda.findAll({
    include: [{
      model: Korisnik,
      attributes: ['id', 'ime', 'prezime', 'email', 'tvrtka']
    }],
    order: [['createdAt', 'DESC']]
  });
};

const getById = async (id) => {
  const ponuda = await Ponuda.findByPk(id, {
    include: [{
      model: Korisnik,
      attributes: ['id', 'ime', 'prezime', 'email', 'tvrtka', 'telefon']
    }]
  });
  if (!ponuda) throw new Error('Ponuda nije pronađena');
  return ponuda;
};

const updateStatus = async (id, status, odgovor) => {
  const ponuda = await Ponuda.findByPk(id);
  if (!ponuda) throw new Error('Ponuda nije pronađena');

  const validStatuses = ['nova', 'poslana', 'odobrena', 'odbijena'];
  if (!validStatuses.includes(status)) {
    throw new Error('Nevažeći status');
  }

  ponuda.status = status;
  if (odgovor !== undefined) {
    ponuda.odgovor = odgovor;
  }
  await ponuda.save();
  return ponuda;
};

module.exports = { getAll, getById, updateStatus };
