'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.sequelize.query(
      "UPDATE users SET role = 'tehnicar' WHERE role = 'radnik';"
    );

    await queryInterface.changeColumn('users', 'role', {
      type: Sequelize.ENUM('admin', 'manager', 'tehnicar'),
      allowNull: false,
      defaultValue: 'tehnicar'
    });
  },

  async down(queryInterface, Sequelize) {
    await queryInterface.sequelize.query(
      "UPDATE users SET role = 'radnik' WHERE role = 'tehnicar';"
    );

    await queryInterface.changeColumn('users', 'role', {
      type: Sequelize.ENUM('admin', 'manager', 'radnik'),
      allowNull: false,
      defaultValue: 'radnik'
    });
  }
};