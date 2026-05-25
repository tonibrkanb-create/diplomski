const db = require('../models');
const bcrypt = require('bcrypt');

class AuthService {
  async register(data) {
    try {
      // will trigger model hook to hash
      return await db.user.create({
        username: data.username,
        password: data.password
      });
    } catch (error) {
      throw new Error(`Error creating user: ${error.message}`);
    }
  }

  async login(username, password) {
    try {
      const user = await db.user.findOne({ where: { username } });
      if (!user) {
        throw new Error('Invalid credentials');
      }
      const match = await bcrypt.compare(password, user.password);
      if (!match) {
        throw new Error('Invalid credentials');
      }
      // return user object sans password
      const { password: pwd, ...safe } = user.toJSON();
      return safe;
    } catch (error) {
      throw new Error(`Login failed: ${error.message}`);
    }
  }

  async korisnikLogin(email, password) {
    try {
      const korisnik = await db.korisnik.findOne({ where: { email } });
      if (!korisnik) {
        throw new Error('Invalid credentials');
      }
      const match = await bcrypt.compare(password, korisnik.password);
      if (!match) {
        throw new Error('Invalid credentials');
      }
      const { password: pwd, ...safe } = korisnik.toJSON();
      return safe;
    } catch (error) {
      throw new Error(`Login failed: ${error.message}`);
    }
  }
}

module.exports = new AuthService();