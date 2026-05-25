'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn('documents', 'blob', {
      type: Sequelize.BLOB('long'),
      allowNull: true
    });
  },

  async down(queryInterface) {
    try {
      await queryInterface.removeColumn('documents', 'blob');
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};