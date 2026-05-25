'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.bulkDelete('ponude', null, {});
    return queryInterface.bulkInsert('ponude', [
      {
        korisnikId: 1,
        opis: 'Trebam attest za novu gradnju poslovnog objekta',
        vrstaAtesta: 'Attest građevine',
        lokacija: 'Zagreb - Miramarska 25',
        zeljeniDatum: new Date('2026-06-01'),
        status: 'nova',
        odgovor: null,
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        korisnikId: 2,
        opis: 'Analiza zagađenosti tla razgraničene parcele',
        vrstaAtesta: 'Attest za tlo',
        lokacija: 'Split - Marmontova 5',
        zeljeniDatum: new Date('2026-06-15'),
        status: 'poslana',
        odgovor: 'Zaprimljeno, obrada u toku.',
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        korisnikId: 3,
        opis: 'Certifikacija industrijske vode',
        vrstaAtesta: 'Attest vode',
        lokacija: 'Rijeka - Riva 10',
        zeljeniDatum: new Date('2026-06-30'),
        status: 'odobrena',
        odgovor: 'Attest odobren, dokument slijedi.',
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ]);
  },

  down: async (queryInterface, Sequelize) => {
    return queryInterface.bulkDelete('ponude', null, {});
  }
};
