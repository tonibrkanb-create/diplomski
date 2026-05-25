module.exports = (sequelize, DataTypes) => {
  const Narucitelj = sequelize.define('narucitelj', {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    name: {
      type: DataTypes.STRING,
      allowNull: false
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
    OIB: {
      type: DataTypes.STRING,
      allowNull: true
    },
    ziroRacun: {
      type: DataTypes.STRING,
      allowNull: true
    },
    ostalo: {
      type: DataTypes.TEXT,
      allowNull: true
    },
    kontaktOsoba: {
      type: DataTypes.STRING,
      allowNull: true
    },
    telefon: {
      type: DataTypes.STRING,
      allowNull: true
    },
    mobitel: {
      type: DataTypes.STRING,
      allowNull: true
    },
    fax: {
      type: DataTypes.STRING,
      allowNull: true
    },
    email: {
      type: DataTypes.STRING,
      allowNull: true
    },
    location: {
      type: DataTypes.STRING,
      allowNull: false
    },
    comment: {
      type: DataTypes.TEXT,
      allowNull: true
    }
  }, {
    timestamps: true,
    tableName: 'narucitelji'
  });

  Narucitelj.associate = (models) => {
    Narucitelj.hasMany(models.radni_nalog, {
      foreignKey: 'narucitelj_id',
      as: 'radniNalozi'
    });
  };

  return Narucitelj;
};
