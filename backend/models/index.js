const fs = require('fs');
const path = require('path');
const { Sequelize } = require('sequelize');
const config = require('../config/database');

const env = process.env.NODE_ENV || 'development';
const dbConfig = config[env];

const sequelize = new Sequelize(
  dbConfig.database,
  dbConfig.username,
  dbConfig.password,
  {
    host: dbConfig.host,
    port: dbConfig.port,
    dialect: dbConfig.dialect,
    logging: false
  }
);

const db = {};

// Import models
const models = [
  'narucitelj',
  'radni_nalog',
  'document',
  'note',
  'user',
  'aktivnost',
  'uskoro_istice',
  'korisnik',
  'ponuda',
  'obavijest',
  'recenzija',
  'sustav_log'
];

models.forEach(model => {
  const modelPath = path.join(__dirname, `${model}.js`);
  if (fs.existsSync(modelPath)) {
    const modelModule = require(modelPath);
    const m = modelModule(sequelize, Sequelize.DataTypes);
    db[m.name] = m;
  }
});

// Setup associations
Object.keys(db).forEach((modelName) => {
  if (db[modelName].associate) {
    db[modelName].associate(db);
  }
});

db.sequelize = sequelize;
db.Sequelize = Sequelize;

module.exports = db;
