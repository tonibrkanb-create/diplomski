'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('uskoro_istice', {
      id: {
        type: Sequelize.INTEGER,
        primaryKey: true,
        autoIncrement: true
      },
      narucitelj_id: {
        type: Sequelize.INTEGER,
        allowNull: false,
        references: {
          model: 'narucitelji',
          key: 'id'
        },
        onUpdate: 'CASCADE',
        onDelete: 'CASCADE'
      },
      radni_nalog_id: {
        type: Sequelize.INTEGER,
        allowNull: false,
        references: {
          model: 'radni_nalozi',
          key: 'id'
        },
        onUpdate: 'CASCADE',
        onDelete: 'CASCADE'
      },
      aktivnost: {
        type: Sequelize.ENUM(
          'ZastitaNaRadu',
          'ZastitaOdPozara',
          'ZastitaOkolisa',
          'OstalaMjerenja'
        ),
        allowNull: false
      },
      datumIsteka: {
        type: Sequelize.DATEONLY,
        allowNull: false
      },
      createdAt: {
        type: Sequelize.DATE,
        defaultValue: Sequelize.fn('now')
      },
      updatedAt: {
        type: Sequelize.DATE,
        defaultValue: Sequelize.fn('now')
      }
    });

    await queryInterface.addConstraint('uskoro_istice', {
      fields: ['radni_nalog_id', 'aktivnost'],
      type: 'unique',
      name: 'uniq_uskoro_istice_radni_nalog_aktivnost'
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('uskoro_istice');

    if (queryInterface.sequelize.getDialect() === 'postgres') {
      await queryInterface.sequelize.query('DROP TYPE IF EXISTS "enum_uskoro_istice_aktivnost";');
    }
  }
};