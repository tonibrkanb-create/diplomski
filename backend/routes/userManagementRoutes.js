const express = require('express');
const router = express.Router();
const authMiddleware = require('../middleware/authMiddleware');
const userManagementController = require('../controllers/userManagementController');

router.get('/', authMiddleware, userManagementController.list);
router.get('/:id', authMiddleware, userManagementController.getById);
router.post('/', authMiddleware, userManagementController.create);
router.put('/:id', authMiddleware, userManagementController.update);
router.put('/:id/deactivate', authMiddleware, userManagementController.deactivate);

module.exports = router;
