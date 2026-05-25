'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.bulkDelete('obavijesti', null, {});
    return queryInterface.bulkInsert('obavijesti', [
      {
        korisnikId: 1,
        naslov: 'Vaša ponuda je zaprimljena',
        poruka: 'Zahvaljujemo na Vašoj ponudi. U najmanje vremenske provedbe ćemo je obraditi i dostaviti Vam odgovor.',
        procitana: true,
        createdAt: new Date('2026-05-10T08:00:00'),
        updatedAt: new Date('2026-05-10T08:00:00')
      },
      {
        korisnikId: 2,
        naslov: 'Trebam precizniju specifikaciju',
        poruka: 'Molimo Vas da dostavite detaljniju specifikaciju za Vašu ponudu kako bismo je mogli obraditi.',
        procitana: false,
        createdAt: new Date('2026-05-18T14:30:00'),
        updatedAt: new Date('2026-05-18T14:30:00')
      },
      {
        korisnikId: 3,
        naslov: 'Attest je spreman',
        poruka: 'Vaš attest je spreman. Molimo da ga preuzimate iz sustava ili ga ćemo poslati porukom.',
        procitana: false,
        createdAt: new Date('2026-05-20T10:15:00'),
        updatedAt: new Date('2026-05-20T10:15:00')
      },
      {
        korisnikId: 1,
        naslov: 'Raspored pregleda',
        poruka: 'Pregled Vaše lokacije je zakazan za 22.05.2026. u 10:00 sati.',
        procitana: true,
        createdAt: new Date('2026-05-19T09:00:00'),
        updatedAt: new Date('2026-05-19T09:00:00')
      }
    ]);
  },

  down: async (queryInterface, Sequelize) => {
    return queryInterface.bulkDelete('obavijesti', null, {});
  }
};
