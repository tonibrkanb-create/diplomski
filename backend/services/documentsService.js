const doc = require('pdfkit');
const db = require('../models');

class DocumentsService {
  parseBase64Blob(blobInput) {
    if (!blobInput) {
      throw new Error('Document blob is required (base64)');
    }

    if (Buffer.isBuffer(blobInput)) {
      return blobInput;
    }

    if (typeof blobInput !== 'string') {
      throw new Error('Document blob must be a base64 string');
    }

    const cleaned = blobInput.startsWith('data:')
      ? blobInput.substring(blobInput.indexOf(',') + 1)
      : blobInput;

    if (!cleaned || cleaned.trim().length === 0) {
      throw new Error('Document blob is empty');
    }

    const normalized = cleaned.replace(/\s/g, '');
    if (!/^[A-Za-z0-9+/]*={0,2}$/.test(normalized)) {
      throw new Error('Document blob must be valid base64');
    }

    return Buffer.from(normalized, 'base64');
  }

  async getDocumentsByRadniNalog(radniNalogId) {
    try {
      const documents = await db.document.findAll({
        where: {
          radni_nalog_id: radniNalogId
        }
      });

      return documents;
    } catch (error) {
      throw new Error(`Error fetching documents: ${error.message}`);
    }
  }

  async addDocument(radniNalogId, data) {
    try {
      // Verify radni nalog exists
      const nalog = await db.radni_nalog.findByPk(radniNalogId);
      if (!nalog) {
        throw new Error('Radni nalog not found');
      }

      if (!data.name) {
        throw new Error('Document name is required');
      }

      if (!data.blob && data.url === undefined) {
        throw new Error('Document blob is required (base64)');
      }

      if (data.url !== undefined && typeof data.url !== 'string') {
        throw new Error('Document url must be a string');
      }
      
      let documentDate = {
          name: data.name,
          radni_nalog_id: radniNalogId
        };

      if(data.blob) {
        let blobValue = this.parseBase64Blob(data.blob);
        documentDate.blob = blobValue;
      }
      else {
        documentDate.url = data.url;
      }

      console.log('Adding document with data:', documentDate);

      return await db.document.create(documentDate);
    } catch (error) {
      throw new Error(`Error adding document: ${error.message}`);
    }
  }

  async getDocumentById(radniNalogId, documentId) {
    try {
      const document = await db.document.findOne({
        where: {
          id: documentId,
          radni_nalog_id: radniNalogId
        }
      });

      if (!document) {
        throw new Error('Document not found');
      }

      return document;
    } catch (error) {
      throw new Error(`Error fetching document: ${error.message}`);
    }
  }

  async deleteDocument(documentId) {
    try {
      const document = await db.document.findByPk(documentId);

      if (!document) {
        throw new Error('Document not found');
      }

      await document.destroy();
      return document;
    } catch (error) {
      throw new Error(`Error deleting document: ${error.message}`);
    }
  }
}

module.exports = new DocumentsService();
