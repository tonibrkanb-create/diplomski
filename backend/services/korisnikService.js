const bcrypt = require('bcrypt');
const db = require('../models');
const Korisnik = db.korisnik;

const register = async ({ ime, prezime, email, telefon, tvrtka, adresa, mjesto, postanskiBroj, drzava, password }) => {
  const existing = await Korisnik.findOne({ where: { email } });
  if (existing) {
    throw new Error('Email je već registriran');
  }

  const korisnik = await Korisnik.create({
    ime, prezime, email, telefon, tvrtka, adresa, mjesto, postanskiBroj, drzava, password
  });

  const { password: _, ...korisnikData } = korisnik.toJSON();
  return korisnikData;
};

const login = async (email, password) => {
  const korisnik = await Korisnik.findOne({ where: { email, isActive: true } });
  if (!korisnik) {
    throw new Error('Neispravni podaci za prijavu');
  }

  const isMatch = await bcrypt.compare(password, korisnik.password);
  if (!isMatch) {
    throw new Error('Neispravni podaci za prijavu');
  }

  const { password: _, ...korisnikData } = korisnik.toJSON();
  return korisnikData;
};

const getProfile = async (korisnikId) => {
  const korisnik = await Korisnik.findByPk(korisnikId, {
    attributes: { exclude: ['password'] }
  });
  if (!korisnik) {
    throw new Error('Korisnik nije pronađen');
  }
  return korisnik;
};

const updateProfile = async (korisnikId, data) => {
  const korisnik = await Korisnik.findByPk(korisnikId);
  if (!korisnik) {
    throw new Error('Korisnik nije pronađen');
  }

  const allowedFields = ['ime', 'prezime', 'telefon', 'tvrtka', 'adresa', 'mjesto', 'postanskiBroj', 'drzava'];
  for (const field of allowedFields) {
    if (data[field] !== undefined) {
      korisnik[field] = data[field];
    }
  }

  await korisnik.save();
  const { password: _, ...korisnikData } = korisnik.toJSON();
  return korisnikData;
};

module.exports = { register, login, getProfile, updateProfile };
