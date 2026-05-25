'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn('radni_nalozi', 'assignedUserId', {
      type: Sequelize.INTEGER,
      allowNull: true,
      references: {
        model: 'users',
        key: 'id'
      },
      onUpdate: 'CASCADE',
      onDelete: 'SET NULL'
    });
  },

  async down(queryInterface, Sequelize) {
    try {
      await queryInterface.removeColumn('radni_nalozi', 'assignedUserId');
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};
