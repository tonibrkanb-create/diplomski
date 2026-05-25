const bcrypt = require('bcrypt');

module.exports = (sequelize, DataTypes) => {
  const Korisnik = sequelize.define('korisnik', {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    ime: {
      type: DataTypes.STRING,
      allowNull: false
    },
    prezime: {
      type: DataTypes.STRING,
      allowNull: false
    },
    email: {
      type: DataTypes.STRING,
      allowNull: false,
      unique: true
    },
    telefon: {
      type: DataTypes.STRING,
      allowNull: true
    },
    tvrtka: {
      type: DataTypes.STRING,
      allowNull: true
    },
    adresa: {
      type: DataTypes.STRING,
      allowNull: true
    },
    mjesto: {
      type: DataTypes.STRING,
      allowNull: true
    },
    postanskiBroj: {
      type: DataTypes.STRING,
      allowNull: true
    },
    drzava: {
      type: DataTypes.STRING,
      allowNull: true
    },
    password: {
      type: DataTypes.STRING,
      allowNull: false
    },
    isActive: {
      type: DataTypes.BOOLEAN,
      defaultValue: true
    }
  }, {
    timestamps: true,
    tableName: 'korisnici',
    hooks: {
      beforeCreate: async (korisnik) => {
        if (korisnik.password) {
          const salt = await bcrypt.genSalt(10);
          korisnik.password = await bcrypt.hash(korisnik.password, salt);
        }
      },
      beforeUpdate: async (korisnik) => {
        if (korisnik.changed('password')) {
          const salt = await bcrypt.genSalt(10);
          korisnik.password = await bcrypt.hash(korisnik.password, salt);
        }
      }
    }
  });

  Korisnik.associate = (models) => {
    Korisnik.hasMany(models.ponuda, { foreignKey: 'korisnikId' });
    Korisnik.hasMany(models.obavijest, { foreignKey: 'korisnikId' });
    Korisnik.hasMany(models.recenzija, { foreignKey: 'korisnikId' });
  };

  return Korisnik;
};
