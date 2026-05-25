module.exports = (sequelize, DataTypes) => {
  const Note = sequelize.define('note', {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    date: {
      type: DataTypes.DATE,
      allowNull: false,
      defaultValue: DataTypes.NOW
    },
    text: {
      type: DataTypes.TEXT,
      allowNull: false
    }
  }, {
    timestamps: true,
    tableName: 'notes'
  });

  Note.associate = (models) => {
    Note.belongsTo(models.radni_nalog, {
      foreignKey: 'radni_nalog_id'
    });
  };

  return Note;
};
