'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.bulkDelete('documents', null, {});
    return queryInterface.bulkInsert('documents', [
      {
        name: 'Istražni izvještaj RN-001',
        url: 'https://example.com/documents/rn-001.pdf',
        radni_nalog_id: 1,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        name: 'Analiza rezultata vode',
        url: 'https://example.com/documents/analiza-vode.pdf',
        radni_nalog_id: 2,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        name: 'Certifikacijski dokument',
        url: 'https://example.com/documents/cert-proizvoda.pdf',
        radni_nalog_id: 3,
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ]);
  },

  down: async (queryInterface, Sequelize) => {
    return queryInterface.bulkDelete('documents', null, {});
  }
};
