'use strict';

module.exports = {
  async up(queryInterface) {
    await queryInterface.removeColumn('documents', 'url');
  },

  async down(queryInterface, Sequelize) {
    try {
      await queryInterface.addColumn('documents', 'url', {
        type: Sequelize.STRING,
        allowNull: false,
        defaultValue: ''
      });
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};