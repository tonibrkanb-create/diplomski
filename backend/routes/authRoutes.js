const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

// registration endpoint
router.post('/register', authController.register);
// login endpoint
router.post('/login', authController.login);
// korisnik login endpoint
router.post('/korisnik-login', authController.korisnikLogin);

module.exports = router;