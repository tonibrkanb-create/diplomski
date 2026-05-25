const jwt = require('jsonwebtoken');
const korisnikService = require('../services/korisnikService');

const JWT_SECRET = process.env.JWT_SECRET || 'change-me-in-production';

function createKorisnikToken(korisnik) {
  return jwt.sign(
    { korisnikId: korisnik.id, email: korisnik.email },
    JWT_SECRET,
    { expiresIn: '24h' }
  );
}

const register = async (req, res) => {
  try {
    const korisnik = await korisnikService.register(req.body);
    const token = createKorisnikToken(korisnik);
    res.status(201).json({ korisnik, token });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

const login = async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) {
      return res.status(400).json({ message: 'Email i lozinka su obavezni' });
    }
    const korisnik = await korisnikService.login(email, password);
    const token = createKorisnikToken(korisnik);
    res.json({ korisnik, token });
  } catch (error) {
    res.status(401).json({ message: error.message });
  }
};

const getProfile = async (req, res) => {
  try {
    const korisnik = await korisnikService.getProfile(req.korisnik.korisnikId);
    res.json(korisnik);
  } catch (error) {
    res.status(404).json({ message: error.message });
  }
};

const updateProfile = async (req, res) => {
  try {
    const korisnik = await korisnikService.updateProfile(req.korisnik.korisnikId, req.body);
    res.json(korisnik);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

module.exports = { register, login, getProfile, updateProfile };
