const db = require('../models');
const Ponuda = db.ponuda;

const getByKorisnik = async (korisnikId) => {
  return Ponuda.findAll({
    where: { korisnikId },
    order: [['createdAt', 'DESC']]
  });
};

const getById = async (id, korisnikId) => {
  const ponuda = await Ponuda.findOne({ where: { id, korisnikId } });
  if (!ponuda) {
    throw new Error('Ponuda nije pronađena');
  }
  return ponuda;
};

const create = async (korisnikId, { opis, vrstaAtesta, lokacija, zeljeniDatum }) => {
  if (!opis || !opis.trim()) {
    throw new Error('Opis je obavezan');
  }

  return Ponuda.create({
    korisnikId,
    opis,
    vrstaAtesta: vrstaAtesta || null,
    lokacija: lokacija || null,
    zeljeniDatum: zeljeniDatum || null,
    status: 'nova'
  });
};

module.exports = { getByKorisnik, getById, create };
