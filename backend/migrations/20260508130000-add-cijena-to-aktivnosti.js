'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn('aktivnosti', 'cijena', {
      type: Sequelize.DECIMAL(10, 2),
      allowNull: true,
      defaultValue: null
    });
  },

  async down(queryInterface, Sequelize) {
    try {
      await queryInterface.removeColumn('aktivnosti', 'cijena');
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};
