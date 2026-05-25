module.exports = (sequelize, DataTypes) => {
  const Document = sequelize.define('document', {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    name: {
      type: DataTypes.STRING,
      allowNull: false
    },
    url: {
      type: DataTypes.STRING,
      allowNull: true
    },
    blob: {
      type: DataTypes.BLOB('long'),
      allowNull: true
    }
  }, {
    timestamps: true,
    tableName: 'documents'
  });

  Document.associate = (models) => {
    Document.belongsTo(models.radni_nalog, {
      foreignKey: 'radni_nalog_id'
    });
  };

  return Document;
};
