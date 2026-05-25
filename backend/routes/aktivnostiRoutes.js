const express = require('express');
const router = express.Router();
const aktivnostiController = require('../controllers/aktivnostiController');
const authMiddleware = require('../middleware/authMiddleware');

router.use(authMiddleware);

// GET all aktivnosti
router.get('/', aktivnostiController.getAll);

// GET aktivnost by ID
router.get('/:id', aktivnostiController.getById);

// POST - Create aktivnost
router.post('/', aktivnostiController.create);

// PUT - Update aktivnost (aktivnost and rokTrajanja only)
router.put('/:id', aktivnostiController.update);

// DELETE aktivnost (soft delete: isActive = false)
router.delete('/:id', aktivnostiController.delete);

module.exports = router;