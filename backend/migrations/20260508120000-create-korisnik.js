'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('korisnici', {
      id: {
        type: Sequelize.INTEGER,
        primaryKey: true,
        autoIncrement: true
      },
      ime: {
        type: Sequelize.STRING,
        allowNull: false
      },
      prezime: {
        type: Sequelize.STRING,
        allowNull: false
      },
      email: {
        type: Sequelize.STRING,
        allowNull: false,
        unique: true
      },
      telefon: {
        type: Sequelize.STRING,
        allowNull: true
      },
      tvrtka: {
        type: Sequelize.STRING,
        allowNull: true
      },
      adresa: {
        type: Sequelize.STRING,
        allowNull: true
      },
      mjesto: {
        type: Sequelize.STRING,
        allowNull: true
      },
      postanskiBroj: {
        type: Sequelize.STRING,
        allowNull: true
      },
      drzava: {
        type: Sequelize.STRING,
        allowNull: true
      },
      password: {
        type: Sequelize.STRING,
        allowNull: false
      },
      isActive: {
        type: Sequelize.BOOLEAN,
        defaultValue: true
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
    await queryInterface.dropTable('korisnici');
  }
};
