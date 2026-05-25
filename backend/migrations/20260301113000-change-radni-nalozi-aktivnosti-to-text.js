'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.changeColumn('radni_nalozi', 'aktivnosti', {
      type: Sequelize.TEXT,
      allowNull: true
    });

    await queryInterface.sequelize.query(`
      UPDATE radni_nalozi
      SET aktivnosti = CONCAT('["', aktivnosti, '"]')
      WHERE aktivnosti IS NOT NULL
        AND aktivnosti NOT LIKE '[%'
    `);
  },

  async down(queryInterface, Sequelize) {
    try {
      await queryInterface.sequelize.query(`
        UPDATE radni_nalozi
        SET aktivnosti = CASE
          WHEN JSON_VALID(aktivnosti) THEN JSON_UNQUOTE(JSON_EXTRACT(aktivnosti, '$[0]'))
          ELSE aktivnosti
        END
        WHERE aktivnosti IS NOT NULL
      `);

      await queryInterface.changeColumn('radni_nalozi', 'aktivnosti', {
        type: Sequelize.ENUM(
          'ZastitaNaRadu',
          'ZastitaOdPozara',
          'ZastitaOkolisa',
          'OstalaMjerenja'
        ),
        allowNull: true
      });
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};