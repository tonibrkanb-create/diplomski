package com.atesti.controller;

import com.atesti.dto.NoteRequest;
import com.atesti.entity.Note;
import com.atesti.service.NotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/radni-nalozi/{radniNalogId}/notes")
@RequiredArgsConstructor
public class NotesController {

    private final NotesService notesService;

    @GetMapping
    public ResponseEntity<List<Note>> getByRadniNalog(@PathVariable Long radniNalogId) {
        return ResponseEntity.ok(notesService.getNotesByRadniNalog(radniNalogId));
    }

    @PostMapping
    public ResponseEntity<Note> add(@PathVariable Long radniNalogId, @RequestBody NoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notesService.addNote(radniNalogId, request));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Note> delete(@PathVariable Long radniNalogId, @PathVariable Long noteId) {
        return ResponseEntity.ok(notesService.deleteNote(noteId));
    }
}
