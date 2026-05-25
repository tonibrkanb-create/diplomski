const express = require('express');
const router = express.Router();
const naruciteljiController = require('../controllers/naruciteljiController');
const authMiddleware = require('../middleware/authMiddleware');

router.use(authMiddleware);

// GET all narucitelji
router.get('/', naruciteljiController.getAll);

// GET narucitelj by ID
router.get('/:id', naruciteljiController.getById);

// POST - Create narucitelj
router.post('/', naruciteljiController.create);

// PUT - Update narucitelj
router.put('/:id', naruciteljiController.update);

// DELETE narucitelj
router.delete('/:id', naruciteljiController.delete);

module.exports = router;
