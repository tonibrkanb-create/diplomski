'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.sequelize.query('SET FOREIGN_KEY_CHECKS=0');
    try {
      await queryInterface.sequelize.query('TRUNCATE TABLE radni_nalozi');
      // Map activity names to IDs
      const aktivnostiRows = await queryInterface.sequelize.query(
        'SELECT id, aktivnost FROM aktivnosti',
        { type: Sequelize.QueryTypes.SELECT }
      );
      const nameToId = {};
      aktivnostiRows.forEach(row => { nameToId[row.aktivnost] = row.id; });

      const radniNaloziData = [
        {
          brojNaloga: 'RN001',
          datum: new Date('2026-05-15'),
          objekt: 'Zgrada A - Zagreb',
          fakturirano: false,
          zavrseno: false,
          opis: 'Inspekcija građevine i energetski audit',
          brojPonude: 'P-2026-001',
          brojRacuna: 'R-2026-001',
          narudzbenica: 'N-2026-001',
          ugovor: 'U-2026-001',
          aktivnosti: JSON.stringify([
            nameToId['Inspekcija građevine'],
            nameToId['Energetski audit']
          ]),
          narucitelj_id: 1,
          assignedUserId: null,
          createdAt: new Date(),
          updatedAt: new Date()
        },
        {
          brojNaloga: 'RN002',
          datum: new Date('2026-05-16'),
          objekt: 'Zgrada B - Split',
          fakturirano: true,
          zavrseno: true,
          opis: 'Testiranje kvalitete vode',
          brojPonude: 'P-2026-002',
          brojRacuna: 'R-2026-002',
          narudzbenica: 'N-2026-002',
          ugovor: 'U-2026-002',
          aktivnosti: JSON.stringify([
            nameToId['Testiranje kvalitete vode']
          ]),
          narucitelj_id: 2,
          assignedUserId: null,
          createdAt: new Date(),
          updatedAt: new Date()
        },
        {
          brojNaloga: 'RN003',
          datum: new Date('2026-05-17'),
          objekt: 'Skladište - Rijeka',
          fakturirano: false,
          zavrseno: false,
          opis: 'Ne volin ić u Rijeku',
          brojPonude: 'P-2026-003',
          brojRacuna: null,
          narudzbenica: 'N-2026-003',
          ugovor: 'U-2026-003',
          aktivnosti: JSON.stringify([
            nameToId['Vatrodojava'],
            nameToId['Certifikacija proizvoda']
          ]),
          narucitelj_id: 3,
          assignedUserId: null,
          createdAt: new Date(),
          updatedAt: new Date()
        }
      ];
      const result = await queryInterface.bulkInsert('radni_nalozi', radniNaloziData);
      await queryInterface.sequelize.query('SET FOREIGN_KEY_CHECKS=1');
      return result;
    } catch (error) {
      await queryInterface.sequelize.query('SET FOREIGN_KEY_CHECKS=1');
      throw error;
    }
  },

  down: async (queryInterface, Sequelize) => {
    return queryInterface.bulkDelete('radni_nalozi', null, {});
  }
};
