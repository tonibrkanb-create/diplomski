module.exports = (sequelize, DataTypes) => {
  const SustavLog = sequelize.define('sustav_log', {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    action: {
      type: DataTypes.STRING,
      allowNull: false
    },
    entity: {
      type: DataTypes.STRING,
      allowNull: false
    },
    entityId: {
      type: DataTypes.INTEGER,
      allowNull: true
    },
    userId: {
      type: DataTypes.INTEGER,
      allowNull: true
    },
    details: {
      type: DataTypes.TEXT,
      allowNull: true
    }
  }, {
    timestamps: true,
    updatedAt: false,
    tableName: 'sustav_logovi'
  });

  SustavLog.associate = (models) => {
    SustavLog.belongsTo(models.user, { foreignKey: 'userId', as: 'user' });
  };

  return SustavLog;
};
