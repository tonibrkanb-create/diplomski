'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.bulkDelete('notes', null, {});
    return queryInterface.bulkInsert('notes', [
      {
        date: new Date('2026-05-15T10:30:00'),
        text: 'Prva inspekcija lokacije - sve u redu',
        radni_nalog_id: 1,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        date: new Date('2026-05-15T14:00:00'),
        text: 'Energetski audit u toku',
        radni_nalog_id: 1,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        date: new Date('2026-05-16T09:00:00'),
        text: 'Testiranje vode - početak procedure',
        radni_nalog_id: 2,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        date: new Date('2026-05-16T16:45:00'),
        text: 'Testiranje vode - završeno',
        radni_nalog_id: 2,
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ]);
  },

  down: async (queryInterface, Sequelize) => {
    return queryInterface.bulkDelete('notes', null, {});
  }
};
