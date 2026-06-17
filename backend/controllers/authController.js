const authService = require('../services/authService');
const jwt = require('jsonwebtoken');

const getJwtSecret = () => process.env.JWT_SECRET || 'change-me-in-production';

const createToken = (user) => jwt.sign(
  { id: user.id, username: user.username, role: user.role },
  getJwtSecret(),
  { expiresIn: '24h' }
);

class AuthController {
  async register(req, res) {
    try {
      const user = await authService.register(req.body);
      const token = createToken(user);
      res.status(201).json({ user, token });
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  }

  async login(req, res) {
    try {
      console.log('Login attempt for username:', req.body.username);
      const user = await authService.login(req.body.username, req.body.password);
      const token = createToken(user);
      let tok = jwt.decode(token, { complete: true });
      console.log('token:', tok);
      console.log('Decoded token payload:', tok.payload.id);
      res.json({ user, token });
    } catch (error) {
      res.status(401).json({ message: error.message });
    }
  }

  async korisnikLogin(req, res) {
    try {
      console.log('Korisnik login attempt for email:', req.body.email);
      const user = await authService.korisnikLogin(req.body.email, req.body.password);
      const token = jwt.sign(
        { id: user.id, email: user.email, role: 'korisnik' },
        getJwtSecret(),
        { expiresIn: '24h' }
      );
      console.log('token:', token);
      res.json({ user, token });
    } catch (error) {
      res.status(401).json({ message: error.message });
    }
  }
}

module.exports = new AuthController();