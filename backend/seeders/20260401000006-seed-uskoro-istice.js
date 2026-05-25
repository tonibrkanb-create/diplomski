'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.bulkDelete('uskoro_istice', null, {});
    return queryInterface.bulkInsert('uskoro_istice', [
      {
        narucitelj_id: 1,
        radni_nalog_id: 1,
        aktivnost: 'Inspekcija građevine',
        datumIsteka: new Date('2026-06-15'),
        isActive: true,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        narucitelj_id: 2,
        radni_nalog_id: 2,
        aktivnost: 'Testiranje kvalitete vode',
        datumIsteka: new Date('2026-05-25'),
        isActive: true,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        narucitelj_id: 3,
        radni_nalog_id: 3,
        aktivnost: 'Certifikacija proizvoda',
        datumIsteka: new Date('2026-07-20'),
        isActive: true,
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ]);
  },

  down: async (queryInterface, Sequelize) => {
    return queryInterface.bulkDelete('uskoro_istice', null, {});
  }
};
