'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn('radni_nalozi', 'pdfUrl', {
      type: Sequelize.STRING,
      allowNull: true
    });
  },

  async down(queryInterface, Sequelize) {
    try {
      await queryInterface.removeColumn('radni_nalozi', 'pdfUrl');
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};