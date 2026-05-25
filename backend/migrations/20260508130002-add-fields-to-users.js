'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn('users', 'ime', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('users', 'prezime', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('users', 'email', {
      type: Sequelize.STRING,
      allowNull: true
    });
    await queryInterface.addColumn('users', 'isActive', {
      type: Sequelize.BOOLEAN,
      defaultValue: true
    });
  },

  async down(queryInterface, Sequelize) {
    try {
      await queryInterface.removeColumn('users', 'ime');
      await queryInterface.removeColumn('users', 'prezime');
      await queryInterface.removeColumn('users', 'email');
      await queryInterface.removeColumn('users', 'isActive');
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};
