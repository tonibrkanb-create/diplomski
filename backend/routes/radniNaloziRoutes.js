const express = require('express');
const router = express.Router();
const radniNaloziController = require('../controllers/radniNaloziController');
const documentsController = require('../controllers/documentsController');
const notesController = require('../controllers/notesController');
const authMiddleware = require('../middleware/authMiddleware');

// PUBLIC: DOWNLOAD RN001 pdf
router.get('/rn001.pdf', radniNaloziController.downloadRn001Pdf);

router.use(authMiddleware);

// GET next available broj naloga
router.get('/nextBrojNaloga', radniNaloziController.getNextBrojNaloga);

// GET uskoro istice list
router.get('/uskoroIstice', radniNaloziController.getUskoroIstice);

// GET all radni nalozi
router.get('/', radniNaloziController.getAll);

// GET radni nalog by ID
router.get('/:id', radniNaloziController.getById);

// DOWNLOAD radni nalog pdf by ID
router.get('/:id/pdf', radniNaloziController.downloadPdf);

// POST - Create radni nalog
router.post('/', radniNaloziController.create);

// PUT - Update radni nalog
router.put('/:id', radniNaloziController.update);

// PUT - Assign worker to radni nalog
router.put('/:id/assign', radniNaloziController.assignWorker);

// DELETE radni nalog
router.delete('/:id', radniNaloziController.delete);

// GET documents for radni nalog
router.get('/:radniNalogId/documents', documentsController.getByRadniNalog);

// POST - Add document to radni nalog
router.post('/:radniNalogId/documents', documentsController.add);

// GET document by ID
router.get('/:radniNalogId/documents/:documentId', documentsController.getById);

// DOWNLOAD document by ID
router.get('/:radniNalogId/documents/:documentId/download', documentsController.download);

// DELETE document from radni nalog
router.delete('/:radniNalogId/documents/:documentId', documentsController.delete);

// GET notes for radni nalog
router.get('/:radniNalogId/notes', notesController.getByRadniNalog);

// POST - Add note to radni nalog
router.post('/:radniNalogId/notes', notesController.add);

// DELETE note from radni nalog
router.delete('/:radniNalogId/notes/:noteId', notesController.delete);

module.exports = router;
