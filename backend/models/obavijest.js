module.exports = (sequelize, DataTypes) => {
  const Obavijest = sequelize.define('obavijest', {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    korisnikId: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    naslov: {
      type: DataTypes.STRING,
      allowNull: false
    },
    poruka: {
      type: DataTypes.TEXT,
      allowNull: false
    },
    procitana: {
      type: DataTypes.BOOLEAN,
      defaultValue: false
    }
  }, {
    timestamps: true,
    tableName: 'obavijesti'
  });

  Obavijest.associate = (models) => {
    Obavijest.belongsTo(models.korisnik, { foreignKey: 'korisnikId' });
  };

  return Obavijest;
};
