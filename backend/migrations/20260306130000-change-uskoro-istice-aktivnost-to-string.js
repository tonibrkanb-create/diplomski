'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.changeColumn('uskoro_istice', 'aktivnost', {
      type: Sequelize.STRING,
      allowNull: false
    });
  },

  async down(queryInterface, Sequelize) {
    try {
      await queryInterface.changeColumn('uskoro_istice', 'aktivnost', {
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
