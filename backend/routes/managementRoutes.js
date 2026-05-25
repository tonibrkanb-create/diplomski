const express = require('express');
const router = express.Router();
const authMiddleware = require('../middleware/authMiddleware');
const ponudeManagementController = require('../controllers/ponudeManagementController');
const recenzijeManagementController = require('../controllers/recenzijeManagementController');
const obavijestiManagementController = require('../controllers/obavijestiManagementController');
const statisticsController = require('../controllers/statisticsController');
const logController = require('../controllers/logController');
const reportsController = require('../controllers/reportsController');

// Ponude
router.get('/ponude', authMiddleware, ponudeManagementController.listAll);
router.get('/ponude/:id', authMiddleware, ponudeManagementController.getById);
router.put('/ponude/:id/status', authMiddleware, ponudeManagementController.updateStatus);

// Recenzije
router.get('/recenzije', authMiddleware, recenzijeManagementController.listAll);
router.put('/recenzije/:id/odgovor', authMiddleware, recenzijeManagementController.respond);

// Obavijesti
router.post('/obavijesti', authMiddleware, obavijestiManagementController.create);
router.get('/korisnici', authMiddleware, obavijestiManagementController.getKorisnici);

// Statistics
router.get('/statistics/dashboard', authMiddleware, statisticsController.getDashboard);
router.get('/statistics/revenue', authMiddleware, statisticsController.getRevenueByAktivnost);
router.get('/statistics/performance', authMiddleware, statisticsController.getPerformanceByWorker);
router.get('/statistics/monthly', authMiddleware, statisticsController.getIssuedByMonth);

// Logs
router.get('/logs', authMiddleware, logController.list);

// Reports
router.get('/reports/nalozi', authMiddleware, reportsController.getNaloziReport);
router.get('/reports/narucitelji', authMiddleware, reportsController.getNaruciteljiReport);

// My tasks (worker)
router.get('/my-tasks', authMiddleware, reportsController.getMyTasks);

module.exports = router;
