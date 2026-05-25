'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.bulkDelete('sustav_logovi', null, {});
    return queryInterface.bulkInsert('sustav_logovi', [
      {
        action: 'CREATE',
        entity: 'radni_nalog',
        entityId: 1,
        userId: null,
        details: 'Kreiraju radni nalog RN-2026-001',
        createdAt: new Date('2026-05-15T08:00:00')
      },
      {
        action: 'UPDATE',
        entity: 'radni_nalog',
        entityId: 2,
        userId: null,
        details: 'Oznaka radni nalog kao završen',
        createdAt: new Date('2026-05-16T17:00:00')
      },
      {
        action: 'CREATE',
        entity: 'ponuda',
        entityId: 1,
        userId: null,
        details: 'Nova ponuda od korisnika Ivan Horvat',
        createdAt: new Date('2026-05-10T10:00:00')
      },
      {
        action: 'UPDATE',
        entity: 'ponuda',
        entityId: 2,
        userId: null,
        details: 'Promjena statusa ponude na poslana',
        createdAt: new Date('2026-05-18T14:30:00')
      },
      {
        action: 'CREATE',
        entity: 'document',
        entityId: 1,
        userId: null,
        details: 'Upload PDF dokumenta - Istražni izvještaj',
        createdAt: new Date('2026-05-15T13:00:00')
      },
      {
        action: 'DELETE',
        entity: 'note',
        entityId: 10,
        userId: null,
        details: 'Brisanje napomene iz radnog naloga',
        createdAt: new Date('2026-05-21T09:30:00')
      }
    ]);
  },

  down: async (queryInterface, Sequelize) => {
    return queryInterface.bulkDelete('sustav_logovi', null, {});
  }
};
