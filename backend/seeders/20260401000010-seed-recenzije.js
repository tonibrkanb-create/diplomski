'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.bulkDelete('recenzije', null, {});
    return queryInterface.bulkInsert('recenzije', [
      {
        korisnikId: 1,
        radniNalogId: 1,
        ocjena: 5,
        komentar: 'Odličan rad, brzo i profesionalno obavljeno. Toplo preporučujem!',
        odgovor: 'Zahvaljujemo na Vašoj recenziji!',
        createdAt: new Date('2026-05-20T15:00:00'),
        updatedAt: new Date('2026-05-20T15:00:00')
      },
      {
        korisnikId: 2,
        radniNalogId: 2,
        ocjena: 4,
        komentar: 'Dobar rad, jedna mali izostanak u dokumentaciji. Zadovoljan sam.',
        odgovor: 'Hvala na povratnoj informaciji, poboljšavamo se.',
        createdAt: new Date('2026-05-20T16:30:00'),
        updatedAt: new Date('2026-05-20T16:30:00')
      },
      {
        korisnikId: 3,
        radniNalogId: null,
        ocjena: 5,
        komentar: 'Sveukupno veoma zadovoljan sa radom i pažnjom prema detaljima.',
        odgovor: null,
        createdAt: new Date('2026-05-21T11:00:00'),
        updatedAt: new Date('2026-05-21T11:00:00')
      }
    ]);
  },

  down: async (queryInterface, Sequelize) => {
    return queryInterface.bulkDelete('recenzije', null, {});
  }
};
