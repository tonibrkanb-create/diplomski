'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn('users', 'role', {
      type: Sequelize.ENUM('admin', 'manager', 'radnik'),
      allowNull: false,
      defaultValue: 'radnik'
    });
  },

  async down(queryInterface, Sequelize) {
    // Try to remove the column if it exists
    try {
      await queryInterface.removeColumn('users', 'role');
    } catch (err) {
      // Ignore error if column does not exist
      console.warn('Column role does not exist or could not be removed:', err.message);
    }
    // Try to drop the ENUM type if it exists
    try {
      await queryInterface.sequelize.query('DROP TYPE IF EXISTS "enum_users_role";');
    } catch (err) {
      console.warn('ENUM type enum_users_role could not be dropped:', err.message);
    }
  }
};
