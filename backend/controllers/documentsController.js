const documentsService = require('../services/documentsService');

class DocumentsController {
  async getByRadniNalog(req, res) {
    try {
      const documents = await documentsService.getDocumentsByRadniNalog(parseInt(req.params.radniNalogId));
      res.json(documents);
    } catch (error) {
      res.status(500).json({ message: error.message });
    }
  }

  async add(req, res) {
    try {
      const document = await documentsService.addDocument(parseInt(req.params.radniNalogId), req.body);
      res.status(201).json(document);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  }

  async getById(req, res) {
    try {
      const document = await documentsService.getDocumentById(
        parseInt(req.params.radniNalogId),
        parseInt(req.params.documentId)
      );

      const payload = document.toJSON();
      payload.blob = document.blob ? document.blob.toString('base64') : null;

      res.json(payload);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }

  async download(req, res) {
    try {
      const document = await documentsService.getDocumentById(
        parseInt(req.params.radniNalogId),
        parseInt(req.params.documentId)
      );

      res.setHeader('Content-Type', 'application/octet-stream');
      res.setHeader('Content-Disposition', `attachment; filename="${document.name}"`);
      res.send(document.blob);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }

  async delete(req, res) {
    try {
      const document = await documentsService.deleteDocument(parseInt(req.params.documentId));
      res.json(document);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }
}

module.exports = new DocumentsController();
