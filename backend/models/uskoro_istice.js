module.exports = (sequelize, DataTypes) => {
  const UskoroIstice = sequelize.define('uskoro_istice', {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    narucitelj_id: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    radni_nalog_id: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    aktivnost: {
      type: DataTypes.STRING,
      allowNull: false
    },
    datumIsteka: {
      type: DataTypes.DATEONLY,
      allowNull: false
    },
    isActive: {
      type: DataTypes.BOOLEAN,
      allowNull: false,
      defaultValue: true
    }
  }, {
    timestamps: true,
    tableName: 'uskoro_istice'
  });

  UskoroIstice.associate = (models) => {
    UskoroIstice.belongsTo(models.narucitelj, {
      foreignKey: 'narucitelj_id',
      as: 'narucitelj'
    });
    UskoroIstice.belongsTo(models.radni_nalog, {
      foreignKey: 'radni_nalog_id',
      as: 'radniNalog'
    });
  };

  return UskoroIstice;
};