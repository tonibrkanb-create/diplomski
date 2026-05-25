'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.changeColumn('aktivnosti', 'aktivnost', {
      type: Sequelize.STRING,
      allowNull: false
    });

    if (queryInterface.sequelize.getDialect() === 'postgres') {
      await queryInterface.sequelize.query('DROP TYPE IF EXISTS "enum_aktivnosti_aktivnost";');
    }
  },

  async down(queryInterface, Sequelize) {
    try {
      await queryInterface.changeColumn('aktivnosti', 'aktivnost', {
        type: Sequelize.ENUM(
          'ZastitaNaRadu',
          'ZastitaOdPozara',
          'ZastitaOkolisa',
          'OstalaMjerenja'
        ),
        allowNull: false
      });
    } catch (error) {
      // Table might have been dropped by earlier migrations
    }
  }
};
