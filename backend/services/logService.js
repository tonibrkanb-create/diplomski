const { Op } = require('sequelize');
const db = require('../models');
const SustavLog = db.sustav_log;
const User = db.user;

const log = async (action, entity, entityId, userId, details) => {
  return SustavLog.create({
    action,
    entity,
    entityId: entityId || null,
    userId: userId || null,
    details: details || null
  });
};

const getAll = async (filters = {}) => {
  const where = {};

  if (filters.entity) where.entity = filters.entity;
  if (filters.action) where.action = filters.action;
  if (filters.from || filters.to) {
    where.createdAt = {};
    if (filters.from) where.createdAt[Op.gte] = new Date(filters.from);
    if (filters.to) where.createdAt[Op.lte] = new Date(filters.to);
  }

  return SustavLog.findAll({
    where,
    include: [{
      model: User,
      as: 'user',
      attributes: ['id', 'username', 'ime', 'prezime']
    }],
    order: [['createdAt', 'DESC']],
    limit: 500
  });
};

module.exports = { log, getAll };
