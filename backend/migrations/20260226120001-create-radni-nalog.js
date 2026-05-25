'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('radni_nalozi', {
      id: {
        type: Sequelize.INTEGER,
        primaryKey: true,
        autoIncrement: true
      },
      brojNaloga: {
        type: Sequelize.STRING,
        allowNull: false,
        unique: true
      },
      narucitelj_id: {
        type: Sequelize.INTEGER,
        allowNull: false,
        references: {
          model: 'narucitelji',
          key: 'id'
        },
        onUpdate: 'CASCADE',
        onDelete: 'CASCADE'
      },
      datum: {
        type: Sequelize.DATE,
        allowNull: false
      },
      objekt: {
        type: Sequelize.STRING,
        allowNull: false
      },
      fakturirano: {
        type: Sequelize.BOOLEAN,
        defaultValue: false
      },
      zavrseno: {
        type: Sequelize.BOOLEAN,
        defaultValue: false
      },
      opis: {
        type: Sequelize.TEXT,
        allowNull: true
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
    await queryInterface.dropTable('radni_nalozi');
  }
};
