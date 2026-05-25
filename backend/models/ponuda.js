module.exports = (sequelize, DataTypes) => {
  const Ponuda = sequelize.define('ponuda', {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    korisnikId: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    opis: {
      type: DataTypes.TEXT,
      allowNull: false
    },
    vrstaAtesta: {
      type: DataTypes.STRING,
      allowNull: true
    },
    lokacija: {
      type: DataTypes.STRING,
      allowNull: true
    },
    zeljeniDatum: {
      type: DataTypes.DATEONLY,
      allowNull: true
    },
    status: {
      type: DataTypes.ENUM('nova', 'poslana', 'odobrena', 'odbijena'),
      defaultValue: 'nova'
    },
    odgovor: {
      type: DataTypes.TEXT,
      allowNull: true
    }
  }, {
    timestamps: true,
    tableName: 'ponude'
  });

  Ponuda.associate = (models) => {
    Ponuda.belongsTo(models.korisnik, { foreignKey: 'korisnikId' });
  };

  return Ponuda;
};
