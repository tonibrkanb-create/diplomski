'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.bulkDelete('aktivnosti', null, {});
    return queryInterface.bulkInsert('aktivnosti', [
      {
        aktivnost: 'Inspekcija građevine',
        rokTrajanja: 30,
        isActive: true,
        cijena: 150.00,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        aktivnost: 'Energetski audit',
        rokTrajanja: 45,
        isActive: true,
        cijena: 200.00,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        aktivnost: 'Testiranje kvalitete vode',
        rokTrajanja: 7,
        isActive: true,
        cijena: 100.00,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        aktivnost: 'Vatrodojava',
        rokTrajanja: 60,
        isActive: true,
        cijena: 250.00,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        aktivnost: 'Certifikacija proizvoda',
        rokTrajanja: 90,
        isActive: true,
        cijena: 300.00,
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ]);
  },

  down: async (queryInterface, Sequelize) => {
    return queryInterface.bulkDelete('aktivnosti', null, {});
  }
};
