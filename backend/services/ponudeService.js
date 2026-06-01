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

const updateStatusByKorisnik = async (id, korisnikId, status) => {
  const ponuda = await Ponuda.findOne({ where: { id, korisnikId } });
  if (!ponuda) {
    throw new Error('Ponuda nije pronađena');
  }

  // Korisnik can only respond to sent offers
  if (ponuda.status !== 'poslana') {
    throw new Error('Ponuda nije dostupna za odgovor');
  }

  const validStatuses = ['odobrena', 'odbijena'];
  if (!validStatuses.includes(status)) {
    throw new Error('Nevažeći status');
  }

  ponuda.status = status;
  await ponuda.save();
  return ponuda;
};

module.exports = { getByKorisnik, getById, create, updateStatusByKorisnik };
