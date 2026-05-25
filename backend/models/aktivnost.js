module.exports = (sequelize, DataTypes) => {
  const Aktivnost = sequelize.define('aktivnost', {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    aktivnost: {
      type: DataTypes.STRING,
      allowNull: false
    },
    rokTrajanja: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    isActive: {
      type: DataTypes.BOOLEAN,
      allowNull: false,
      defaultValue: true
    },
    cijena: {
      type: DataTypes.DECIMAL(10, 2),
      allowNull: true,
      defaultValue: null
    }
  }, {
    timestamps: true,
    tableName: 'aktivnosti'
  });

  return Aktivnost;
};