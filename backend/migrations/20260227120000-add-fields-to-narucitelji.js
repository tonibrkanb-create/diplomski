'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn('narucitelji', 'adresa', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('narucitelji', 'mjesto', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('narucitelji', 'postanskiBroj', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('narucitelji', 'drzava', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('narucitelji', 'OIB', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('narucitelji', 'ziroRacun', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('narucitelji', 'ostalo', {
      type: Sequelize.TEXT,
      allowNull: true
    });
    await queryInterface.addColumn('narucitelji', 'kontaktOsoba', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('narucitelji', 'telefon', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('narucitelji', 'mobitel', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('narucitelji', 'fax', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('narucitelji', 'email', {
      type: Sequelize.STRING,
      allowNull: true
    });
  },

  async down(queryInterface, Sequelize) {
    try {
      await queryInterface.removeColumn('narucitelji', 'email');
      await queryInterface.removeColumn('narucitelji', 'fax');
      await queryInterface.removeColumn('narucitelji', 'mobitel');
      await queryInterface.removeColumn('narucitelji', 'telefon');
      await queryInterface.removeColumn('narucitelji', 'kontaktOsoba');
      await queryInterface.removeColumn('narucitelji', 'ostalo');
      await queryInterface.removeColumn('narucitelji', 'ziroRacun');
      await queryInterface.removeColumn('narucitelji', 'OIB');
      await queryInterface.removeColumn('narucitelji', 'drzava');
      await queryInterface.removeColumn('narucitelji', 'postanskiBroj');
      await queryInterface.removeColumn('narucitelji', 'mjesto');
      await queryInterface.removeColumn('narucitelji', 'adresa');
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};