'use strict';
const bcrypt = require('bcrypt');

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.sequelize.query('SET FOREIGN_KEY_CHECKS=0');
    try {
      await queryInterface.sequelize.query('TRUNCATE TABLE korisnici');
      const salt = await bcrypt.genSalt(10);
      const hashedPassword = await bcrypt.hash('korisnik123', salt);

      const result = await queryInterface.bulkInsert('korisnici', [
        {
          ime: 'Ivan',
          prezime: 'Horvat',
          email: 'ivan.horvat@example.com',
          telefon: '+385 1 1234567',
          tvrtka: 'Gradnja d.o.o.',
          adresa: 'Miramarska 25',
          mjesto: 'Zagreb',
          postanskiBroj: '10000',
          drzava: 'Hrvatska',
          password: hashedPassword,
          isActive: true,
          createdAt: new Date(),
          updatedAt: new Date()
        },
        {
          ime: 'Petra',
          prezime: 'Milanovic',
          email: 'petra.milanovic@example.com',
          telefon: '+385 21 7654321',
          tvrtka: 'Inženjering d.o.o.',
          adresa: 'Marmontova 5',
          mjesto: 'Split',
          postanskiBroj: '21000',
          drzava: 'Hrvatska',
          password: hashedPassword,
          isActive: true,
          createdAt: new Date(),
          updatedAt: new Date()
        },
        {
          ime: 'Ivan',
          prezime: 'Stankovic',
          email: 'ivan.stankovic@example.com',
          telefon: '+385 51 4561234',
          tvrtka: 'Industrijalizacija d.o.o.',
          adresa: 'Riva 10',
          mjesto: 'Rijeka',
          postanskiBroj: '51000',
          drzava: 'Hrvatska',
          password: hashedPassword,
          isActive: true,
          createdAt: new Date(),
          updatedAt: new Date()
        }
      ]);
      await queryInterface.sequelize.query('SET FOREIGN_KEY_CHECKS=1');
      return result;
    } catch (error) {
      await queryInterface.sequelize.query('SET FOREIGN_KEY_CHECKS=1');
      throw error;
    }
  },

  down: async (queryInterface, Sequelize) => {
    return queryInterface.bulkDelete('korisnici', null, {});
  }
};
