const express = require('express');
const router = express.Router();
const radniNaloziController = require('../controllers/radniNaloziController');
const authMiddleware = require('../middleware/authMiddleware');

router.use(authMiddleware);

// GET radni nalozi by narucitelj
router.get('/:naruciteljiId/radni-nalozi', radniNaloziController.getByNarucitelj);

module.exports = router;
