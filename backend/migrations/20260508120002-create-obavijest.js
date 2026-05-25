'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('obavijesti', {
      id: {
        type: Sequelize.INTEGER,
        primaryKey: true,
        autoIncrement: true
      },
      korisnikId: {
        type: Sequelize.INTEGER,
        allowNull: false,
        references: {
          model: 'korisnici',
          key: 'id'
        },
        onUpdate: 'CASCADE',
        onDelete: 'CASCADE'
      },
      naslov: {
        type: Sequelize.STRING,
        allowNull: false
      },
      poruka: {
        type: Sequelize.TEXT,
        allowNull: false
      },
      procitana: {
        type: Sequelize.BOOLEAN,
        defaultValue: false
      },
      createdAt: {
        type: Sequelize.DATE,
        defaultValue: Sequelize.fn('now')
      },
      updatedAt: {
        type: Sequelize.DATE,
        defaultValue: Sequelize.fn('now')
      }
    });
  },

  async down(queryInterface, Sequelize) {
    await queryInterface.dropTable('obavijesti');
  }
};
