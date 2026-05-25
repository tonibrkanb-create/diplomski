module.exports = (sequelize, DataTypes) => {
  const Recenzija = sequelize.define('recenzija', {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    korisnikId: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    radniNalogId: {
      type: DataTypes.INTEGER,
      allowNull: true
    },
    ocjena: {
      type: DataTypes.INTEGER,
      allowNull: false,
      validate: {
        min: 1,
        max: 5
      }
    },
    komentar: {
      type: DataTypes.TEXT,
      allowNull: true
    },
    odgovor: {
      type: DataTypes.TEXT,
      allowNull: true
    }
  }, {
    timestamps: true,
    tableName: 'recenzije'
  });

  Recenzija.associate = (models) => {
    Recenzija.belongsTo(models.korisnik, { foreignKey: 'korisnikId' });
    Recenzija.belongsTo(models.radni_nalog, { foreignKey: 'radniNalogId' });
  };

  return Recenzija;
};
