const db = require('../models');
const User = db.user;
const ALLOWED_ROLES = ['admin', 'manager', 'tehnicar'];

const normalizeRole = (role) => {
  if (role === undefined) {
    return undefined;
  }

  if (!ALLOWED_ROLES.includes(role)) {
    throw new Error(`Neispravna rola. Dozvoljene role su: ${ALLOWED_ROLES.join(', ')}`);
  }

  return role;
};

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

const create = async ({ username, password, ime, prezime, email, role }) => {
  if (!username || !password) {
    throw new Error('Korisničko ime i lozinka su obavezni');
  }

  const existing = await User.findOne({ where: { username } });
  if (existing) throw new Error('Korisničko ime je zauzeto');

  const user = await User.create({
    username,
    password,
    ime,
    prezime,
    email,
    role: normalizeRole(role) || 'tehnicar'
  });
  const { password: _, ...userData } = user.toJSON();
  return userData;
};

const update = async (id, data) => {
  const user = await User.findByPk(id);
  if (!user) throw new Error('Korisnik nije pronađen');

  const allowedFields = ['username', 'ime', 'prezime', 'email', 'role'];
  for (const field of allowedFields) {
    if (data[field] !== undefined) {
      if (field === 'role') {
        user[field] = normalizeRole(data[field]);
        continue;
      }

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
