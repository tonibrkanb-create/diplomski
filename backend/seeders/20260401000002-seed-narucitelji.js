'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.sequelize.query('SET FOREIGN_KEY_CHECKS=0');
    try {
      await queryInterface.sequelize.query('TRUNCATE TABLE narucitelji');
      const result = await queryInterface.bulkInsert('narucitelji', [
        {
          name: 'Gradska općina Zagreb',
          adresa: 'ul. Bana Jelačića 55',
          mjesto: 'Zagreb',
          postanskiBroj: '10000',
          drzava: 'Hrvatska',
          OIB: '12345678901',
          ziroRacun: '1234567890',
          kontaktOsoba: 'Petar Horvat',
          telefon: '+385 1 1234567',
          mobitel: '+385 98 1234567',
          fax: '+385 1 1234568',
          email: 'info@zagreb.hr',
          location: 'Zagreb',
          comment: 'Javna ustanova',
          createdAt: new Date(),
          updatedAt: new Date()
        },
        {
          name: 'Gradska općina Split',
          adresa: 'Ulica krallja Tomislava 1',
          mjesto: 'Split',
          postanskiBroj: '21000',
          drzava: 'Hrvatska',
          OIB: '12345678902',
          ziroRacun: '1234567891',
          kontaktOsoba: 'Marko Milanovic',
          telefon: '+385 21 1234567',
          mobitel: '+385 98 7654321',
          fax: '+385 21 1234568',
          email: 'info@split.hr',
          location: 'Split',
          comment: 'Javna ustanova',
          createdAt: new Date(),
          updatedAt: new Date()
        },
        {
          name: 'Vanjska trgovina d.o.o.',
          adresa: 'Prilika 25',
          mjesto: 'Rijeka',
          postanskiBroj: '51000',
          drzava: 'Hrvatska',
          OIB: '12345678903',
          ziroRacun: '1234567892',
          kontaktOsoba: 'Jovan Stankovic',
          telefon: '+385 51 1234567',
          mobitel: '+385 99 1234567',
          fax: '+385 51 1234568',
          email: 'info@vanjska-trgovina.hr',
          location: 'Rijeka',
          comment: 'Privatno poduzeće',
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
    return queryInterface.bulkDelete('narucitelji', null, {});
  }
};
