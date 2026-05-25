'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn('uskoro_istice', 'isActive', {
      type: Sequelize.BOOLEAN,
      allowNull: false,
      defaultValue: true
    });
  },

  async down(queryInterface) {
    try {
      await queryInterface.removeColumn('uskoro_istice', 'isActive');
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};