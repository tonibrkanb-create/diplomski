const notesService = require('../services/notesService');

class NotesController {
  async getByRadniNalog(req, res) {
    try {
      const notes = await notesService.getNotesByRadniNalog(parseInt(req.params.radniNalogId));
      res.json(notes);
    } catch (error) {
      res.status(500).json({ message: error.message });
    }
  }

  async add(req, res) {
    try {
      const note = await notesService.addNote(parseInt(req.params.radniNalogId), req.body);
      res.status(201).json(note);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  }

  async delete(req, res) {
    try {
      const note = await notesService.deleteNote(parseInt(req.params.noteId));
      res.json(note);
    } catch (error) {
      res.status(404).json({ message: error.message });
    }
  }
}

module.exports = new NotesController();
