const db = require('../models');
const User = db.user;

const getAll = async () => {
  return User.findAll({
    attributes: { exclude: ['password'] },
    order: [['id', 'ASC']]
  });
};

const getById = async (id) => {
  const user = await User.findByPk(id, {
    attributes: { exclude: ['password'] }
  });
  if (!user) throw new Error('Korisnik nije pronađen');
  return user;
};

const create = async ({ username, password, ime, prezime, email }) => {
  if (!username || !password) {
    throw new Error('Korisničko ime i lozinka su obavezni');
  }

  const existing = await User.findOne({ where: { username } });
  if (existing) throw new Error('Korisničko ime je zauzeto');

  const user = await User.create({ username, password, ime, prezime, email });
  const { password: _, ...userData } = user.toJSON();
  return userData;
};

const update = async (id, data) => {
  const user = await User.findByPk(id);
  if (!user) throw new Error('Korisnik nije pronađen');

  const allowedFields = ['username', 'ime', 'prezime', 'email'];
  for (const field of allowedFields) {
    if (data[field] !== undefined) {
      user[field] = data[field];
    }
  }

  if (data.password) {
    user.password = data.password;
  }

  await user.save();
  const { password: _, ...userData } = user.toJSON();
  return userData;
};

const deactivate = async (id) => {
  const user = await User.findByPk(id);
  if (!user) throw new Error('Korisnik nije pronađen');

  user.isActive = false;
  await user.save();
  const { password: _, ...userData } = user.toJSON();
  return userData;
};

module.exports = { getAll, getById, create, update, deactivate };
