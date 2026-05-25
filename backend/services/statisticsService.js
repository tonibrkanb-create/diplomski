const { Sequelize } = require('sequelize');
const db = require('../models');
const RadniNalog = db.radni_nalog;
const Narucitelj = db.narucitelj;
const Aktivnost = db.aktivnost;
const User = db.user;

const getDashboardStats = async () => {
  const totalNalozi = await RadniNalog.count();
  const totalNarucitelji = await Narucitelj.count();
  const fakturirano = await RadniNalog.count({ where: { fakturirano: true } });
  const zavrseno = await RadniNalog.count({ where: { zavrseno: true } });
  const nefakturirano = await RadniNalog.count({ where: { fakturirano: false } });
  const uTijeku = await RadniNalog.count({ where: { zavrseno: false } });

  return { totalNalozi, totalNarucitelji, fakturirano, zavrseno, nefakturirano, uTijeku };
};

const getRevenueByAktivnost = async () => {
  const aktivnosti = await Aktivnost.findAll({
    where: { isActive: true },
    attributes: ['id', 'aktivnost', 'cijena'],
    order: [['aktivnost', 'ASC']]
  });

  const nalozi = await RadniNalog.findAll({ attributes: ['aktivnosti'] });

  // Count by activity ID
  const countById = {};
  for (const nalog of nalozi) {
    let aktivnostiArr = [];
    if (typeof nalog.aktivnosti === 'string') {
      try {
        aktivnostiArr = JSON.parse(nalog.aktivnosti);
      } catch {
        aktivnostiArr = [];
      }
    } else if (Array.isArray(nalog.aktivnosti)) {
      aktivnostiArr = nalog.aktivnosti;
    }
    for (const id of aktivnostiArr) {
      if (id) countById[id] = (countById[id] || 0) + 1;
    }
  }

  return aktivnosti.map(a => {
    const count = countById[a.id] || 0;
    const cijena = a.cijena ? parseFloat(a.cijena) : 0;
    return {
      id: a.id,
      aktivnost: a.aktivnost,
      cijena,
      count,
      ukupno: cijena * count
    };
  });
};

const getPerformanceByWorker = async () => {
  const users = await User.findAll({
    attributes: { exclude: ['password'] }
  });

  const nalozi = await RadniNalog.findAll({
    where: { assignedUserId: { [Sequelize.Op.ne]: null } },
    attributes: ['assignedUserId', 'zavrseno', 'fakturirano']
  });

  const statsById = {};
  for (const nalog of nalozi) {
    const uid = nalog.assignedUserId;
    if (!statsById[uid]) statsById[uid] = { total: 0, zavrseno: 0, fakturirano: 0 };
    statsById[uid].total++;
    if (nalog.zavrseno) statsById[uid].zavrseno++;
    if (nalog.fakturirano) statsById[uid].fakturirano++;
  }

  return users.map(u => ({
    id: u.id,
    username: u.username,
    ime: u.ime,
    prezime: u.prezime,
    total: statsById[u.id]?.total || 0,
    zavrseno: statsById[u.id]?.zavrseno || 0,
    fakturirano: statsById[u.id]?.fakturirano || 0
  }));
};

const getIssuedByMonth = async () => {
  const nalozi = await RadniNalog.findAll({
    attributes: ['datum'],
    order: [['datum', 'ASC']]
  });

  const byMonth = {};
  for (const nalog of nalozi) {
    if (!nalog.datum) continue;
    const d = new Date(nalog.datum);
    const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
    byMonth[key] = (byMonth[key] || 0) + 1;
  }

  return Object.entries(byMonth).map(([month, count]) => ({ month, count }));
};

module.exports = { getDashboardStats, getRevenueByAktivnost, getPerformanceByWorker, getIssuedByMonth };
