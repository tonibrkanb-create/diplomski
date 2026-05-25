const { Op } = require('sequelize');
const db = require('../models');
const RadniNalog = db.radni_nalog;
const Narucitelj = db.narucitelj;
const User = db.user;

const getNaloziReport = async (filters = {}) => {
  const where = {};

  if (filters.from || filters.to) {
    where.datum = {};
    if (filters.from) where.datum[Op.gte] = new Date(filters.from);
    if (filters.to) where.datum[Op.lte] = new Date(filters.to);
  }
  if (filters.status) {
    if (filters.status === 'fakturiran') where.fakturirano = true;
    else if (filters.status === 'zavrsen') { where.zavrseno = true; where.fakturirano = false; }
    else if (filters.status === 'aktivan') { where.zavrseno = false; where.fakturirano = false; }
  }

  const nalozi = await RadniNalog.findAll({
    where,
    include: [
      { model: Narucitelj, as: 'narucitelj', attributes: ['id', 'name'] },
      { model: User, as: 'assignedUser', attributes: ['id', 'username', 'ime', 'prezime'], required: false }
    ],
    order: [['datum', 'DESC']]
  });

  return nalozi.map(n => {
      const plain = n.toJSON();
      let aktivnostiArray = plain.aktivnosti;
      if (typeof aktivnostiArray === 'string') {
        aktivnostiArray = JSON.parse(aktivnostiArray);
      }
      const assigned = plain.assignedUser;
      return {
        id: plain.id,
        datum: plain.datum,
        status: plain.fakturirano ? 'fakturiran' : plain.zavrseno ? 'zavrsen' : 'aktivan',
        naruciteljNaziv: plain.narucitelj?.name || '-',
        assignedUser: assigned ? `${assigned.ime || ''} ${assigned.prezime || ''}`.trim() || assigned.username : null,
        aktivnostiCount: Array.isArray(aktivnostiArray) ? aktivnostiArray.length : 0
      };
  }
)};

const getNaruciteljiReport = async () => {
  const narucitelji = await Narucitelj.findAll({
    attributes: ['id', 'name', 'adresa', 'kontaktOsoba'],
    order: [['name', 'ASC']]
  });

  const nalozi = await RadniNalog.findAll({
    attributes: ['narucitelj_id']
  });

  const countById = {};
  for (const nalog of nalozi) {
    const nid = nalog.getDataValue('narucitelj_id');
    if (!nid) continue;
    countById[nid] = (countById[nid] || 0) + 1;
  }

  return narucitelji.map(n => ({
    id: n.id,
    naziv: n.name,
    adresa: n.adresa || '-',
    kontakt: n.kontaktOsoba || '-',
    naloziCount: countById[n.id] || 0
  }));
};

const getMyTasks = async (userId) => {
  const nalozi = await RadniNalog.findAll({
    where: { assignedUserId: userId },
    include: [
      { model: Narucitelj, as: 'narucitelj', attributes: ['id', 'name'] }
    ],
    order: [['datum', 'DESC']]
  });

  return nalozi.map(n => {
    const plain = n.toJSON();
    let aktivnostiArray = plain.aktivnosti;
    if (typeof aktivnostiArray === 'string') {
      try {
        aktivnostiArray = JSON.parse(aktivnostiArray);
      } catch (e) {
        aktivnostiArray = [];
      }
    }
    return {
      id: plain.id,
      datum: plain.datum,
      status: plain.fakturirano ? 'fakturiran' : plain.zavrseno ? 'zavrsen' : 'aktivan',
      naruciteljNaziv: plain.narucitelj?.name || '-',
      aktivnostiCount: Array.isArray(aktivnostiArray) ? aktivnostiArray.length : 0
    };
  });
};

module.exports = { getNaloziReport, getNaruciteljiReport, getMyTasks };
