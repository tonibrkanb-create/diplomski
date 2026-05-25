'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn('documents', 'url', {
      type: Sequelize.STRING,
      allowNull: true
    });
  },

  async down(queryInterface) {
    try {
      await queryInterface.removeColumn('documents', 'url');
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};
