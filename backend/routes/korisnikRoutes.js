const express = require('express');
const router = express.Router();
const korisnikAuth = require('../middleware/korisnikAuthMiddleware');
const korisnikController = require('../controllers/korisnikController');
const ponudeController = require('../controllers/ponudeController');
const obavijestiController = require('../controllers/obavijestiController');
const recenzijeController = require('../controllers/recenzijeController');

// Auth (public)
router.post('/register', korisnikController.register);
router.post('/login', korisnikController.login);

// Profile (authenticated)
router.get('/profil', korisnikAuth, korisnikController.getProfile);
router.put('/profil', korisnikAuth, korisnikController.updateProfile);

// Ponude (authenticated)
router.get('/ponude', korisnikAuth, ponudeController.list);
router.post('/ponude', korisnikAuth, ponudeController.create);
router.get('/ponude/:id', korisnikAuth, ponudeController.getById);
router.put('/ponude/:id/status', korisnikAuth, ponudeController.updateStatus);

// Obavijesti (authenticated)
router.get('/obavijesti', korisnikAuth, obavijestiController.list);
router.put('/obavijesti/:id/procitaj', korisnikAuth, obavijestiController.markAsRead);

// Recenzije (authenticated)
router.get('/recenzije', korisnikAuth, recenzijeController.list);
router.post('/recenzije', korisnikAuth, recenzijeController.create);
router.get('/recenzije/:id', korisnikAuth, recenzijeController.getById);

module.exports = router;
