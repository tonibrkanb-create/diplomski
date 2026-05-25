const db = require('../models');

class NotesService {
  async getNotesByRadniNalog(radniNalogId) {
    try {
      const notes = await db.note.findAll({
        where: {
          radni_nalog_id: radniNalogId
        },
        order: [['date', 'DESC']]
      });

      return notes;
    } catch (error) {
      throw new Error(`Error fetching notes: ${error.message}`);
    }
  }

  async addNote(radniNalogId, data) {
    try {
      // Verify radni nalog exists
      const nalog = await db.radni_nalog.findByPk(radniNalogId);
      if (!nalog) {
        throw new Error('Radni nalog not found');
      }

      return await db.note.create({
        date: data.date ? new Date(data.date) : new Date(),
        text: data.text,
        radni_nalog_id: radniNalogId
      });
    } catch (error) {
      throw new Error(`Error adding note: ${error.message}`);
    }
  }

  async deleteNote(noteId) {
    try {
      const note = await db.note.findByPk(noteId);

      if (!note) {
        throw new Error('Note not found');
      }

      await note.destroy();
      return note;
    } catch (error) {
      throw new Error(`Error deleting note: ${error.message}`);
    }
  }
}

module.exports = new NotesService();
