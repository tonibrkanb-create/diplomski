'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('ponude', {
      id: {
        type: Sequelize.INTEGER,
        primaryKey: true,
        autoIncrement: true
      },
      korisnikId: {
        type: Sequelize.INTEGER,
        allowNull: false,
        references: {
          model: 'korisnici',
          key: 'id'
        },
        onUpdate: 'CASCADE',
        onDelete: 'CASCADE'
      },
      opis: {
        type: Sequelize.TEXT,
        allowNull: false
      },
      vrstaAtesta: {
        type: Sequelize.STRING,
        allowNull: true
      },
      lokacija: {
        type: Sequelize.STRING,
        allowNull: true
      },
      zeljeniDatum: {
        type: Sequelize.DATEONLY,
        allowNull: true
      },
      status: {
        type: Sequelize.ENUM('nova', 'poslana', 'odobrena', 'odbijena'),
        defaultValue: 'nova'
      },
      odgovor: {
        type: Sequelize.TEXT,
        allowNull: true
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
  },

  async down(queryInterface, Sequelize) {
    await queryInterface.dropTable('ponude');
  }
};
