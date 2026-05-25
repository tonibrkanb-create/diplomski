'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    // Drop all tables
    await queryInterface.dropAllTables();
  },

  down: async (queryInterface, Sequelize) => {
    // No-op for down migration
  }
};
