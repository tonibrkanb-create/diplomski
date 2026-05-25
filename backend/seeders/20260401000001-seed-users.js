'use strict';
const bcrypt = require('bcrypt');

module.exports = {
  up: async (queryInterface, Sequelize) => {
    // Clear existing users
    await queryInterface.bulkDelete('users', null, {});

    const password1 = await bcrypt.hash('password123', 10);
    const password2 = await bcrypt.hash('password123', 10);
    const password3 = await bcrypt.hash('password123', 10);

    try {
      return await queryInterface.bulkInsert('users', [
        {
          username: 'admin',
          password: password1,
          ime: 'Admin',
          prezime: 'User',
          email: 'admin@mateategibe.com',
          isActive: true,
          createdAt: new Date(),
          updatedAt: new Date()
        },
        {
          username: 'korisnik1',
          password: password2,
          ime: 'Marko',
          prezime: 'Horvat',
          email: 'marko@mateategibe.com',
          isActive: true,
          createdAt: new Date(),
          updatedAt: new Date()
        },
        {
          username: 'korisnik2',
          password: password3,
          ime: 'Ana',
          prezime: 'Horvat',
          email: 'ana@mateategibe.com',
          isActive: true,
          createdAt: new Date(),
          updatedAt: new Date()
        }
      ], { validate: false });
    } catch (error) {
      console.error('Seeder error:', error);
      throw error;
    }
  },

  down: async (queryInterface, Sequelize) => {
    return queryInterface.bulkDelete('users', null, {});
  }
};
