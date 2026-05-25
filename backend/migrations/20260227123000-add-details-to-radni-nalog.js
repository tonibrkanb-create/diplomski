'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn('radni_nalozi', 'brojPonude', {
      type: Sequelize.STRING,
      allowNull: true
    });

    await queryInterface.addColumn('radni_nalozi', 'brojRacuna', {
      type: Sequelize.STRING,
      allowNull: true
    });

    await queryInterface.addColumn('radni_nalozi', 'narudzbenica', {
      type: Sequelize.STRING,
      allowNull: true
    });

    await queryInterface.addColumn('radni_nalozi', 'ugovor', {
      type: Sequelize.STRING,
      allowNull: true
    });

    await queryInterface.addColumn('radni_nalozi', 'aktivnosti', {
      type: Sequelize.ENUM(
        'ZastitaNaRadu',
        'ZastitaOdPozara',
        'ZastitaOkolisa',
        'OstalaMjerenja'
      ),
      allowNull: true
    });
  },

  async down(queryInterface, Sequelize) {
    try {
      await queryInterface.removeColumn('radni_nalozi', 'aktivnosti');
      await queryInterface.removeColumn('radni_nalozi', 'ugovor');
      await queryInterface.removeColumn('radni_nalozi', 'narudzbenica');
      await queryInterface.removeColumn('radni_nalozi', 'brojRacuna');
      await queryInterface.removeColumn('radni_nalozi', 'brojPonude');

      if (queryInterface.sequelize.getDialect() === 'postgres') {
        await queryInterface.sequelize.query('DROP TYPE IF EXISTS "enum_radni_nalozi_aktivnosti";');
      }
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};