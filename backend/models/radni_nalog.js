module.exports = (sequelize, DataTypes) => {
  const RadniNalog = sequelize.define('radni_nalog', {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    brojNaloga: {
      type: DataTypes.STRING,
      allowNull: false,
      unique: true
    },
    datum: {
      type: DataTypes.DATE,
      allowNull: false
    },
    objekt: {
      type: DataTypes.STRING,
      allowNull: false
    },
    fakturirano: {
      type: DataTypes.BOOLEAN,
      defaultValue: false
    },
    zavrseno: {
      type: DataTypes.BOOLEAN,
      defaultValue: false
    },
    opis: {
      type: DataTypes.TEXT,
      allowNull: true
    },
    brojPonude: {
      type: DataTypes.STRING,
      allowNull: true
    },
    brojRacuna: {
      type: DataTypes.STRING,
      allowNull: true
    },
    narudzbenica: {
      type: DataTypes.STRING,
      allowNull: true
    },
    ugovor: {
      type: DataTypes.STRING,
      allowNull: true
    },
    aktivnosti: {
      type: DataTypes.TEXT,
      allowNull: true,
      get() {
        const rawValue = this.getDataValue('aktivnosti');
        if (!rawValue) {
          return [];
        }

        if (Array.isArray(rawValue)) {
          return rawValue;
        }

        if (typeof rawValue === 'string') {
          try {
            const parsed = JSON.parse(rawValue);
            return Array.isArray(parsed) ? parsed : [rawValue];
          } catch (error) {
            return [rawValue];
          }
        }

        return [];
      },
      set(value) {
        if (value === null || value === undefined) {
          this.setDataValue('aktivnosti', null);
          return;
        }

        if (Array.isArray(value)) {
          this.setDataValue('aktivnosti', JSON.stringify(value));
          return;
        }

        if (typeof value === 'string') {
          try {
            const parsed = JSON.parse(value);
            if (Array.isArray(parsed)) {
              this.setDataValue('aktivnosti', JSON.stringify(parsed));
              return;
            }
          } catch (error) {
          }
          this.setDataValue('aktivnosti', JSON.stringify([value]));
          return;
        }

        this.setDataValue('aktivnosti', JSON.stringify([]));
      }
    },
    pdfUrl: {
      type: DataTypes.STRING,
      allowNull: true
    },
    assignedUserId: {
      type: DataTypes.INTEGER,
      allowNull: true
    }
  }, {
    timestamps: true,
    tableName: 'radni_nalozi'
  });

  RadniNalog.associate = (models) => {
    RadniNalog.belongsTo(models.narucitelj, {
      foreignKey: 'narucitelj_id',
      as: 'narucitelj'
    });
    RadniNalog.hasMany(models.document, {
      foreignKey: 'radni_nalog_id',
      as: 'documents'
    });
    RadniNalog.hasMany(models.note, {
      foreignKey: 'radni_nalog_id',
      as: 'notes'
    });
    RadniNalog.belongsTo(models.user, {
      foreignKey: 'assignedUserId',
      as: 'assignedUser'
    });
  };

  return RadniNalog;
};
