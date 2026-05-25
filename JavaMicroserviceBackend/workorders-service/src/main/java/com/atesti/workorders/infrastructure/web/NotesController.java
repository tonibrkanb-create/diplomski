package com.atesti.workorders.infrastructure.web;

import com.atesti.workorders.application.command.NoteCommandService;
import com.atesti.workorders.application.dto.command.AddNoteCommand;
import com.atesti.workorders.application.dto.query.NoteResponse;
import com.atesti.workorders.domain.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/radni-nalozi/{radniNalogId}/notes")
@RequiredArgsConstructor
public class NotesController {

    private final NoteCommandService commandService;
    private final NoteRepository noteRepository;

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getByRadniNalog(@PathVariable Long radniNalogId) {
        List<NoteResponse> notes = noteRepository.findByRadniNalogIdOrderByDateDesc(radniNalogId).stream()
                .map(NoteResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    public ResponseEntity<NoteResponse> add(@PathVariable Long radniNalogId, @RequestBody AddNoteCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.add(radniNalogId, command));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<NoteResponse> delete(@PathVariable Long radniNalogId, @PathVariable Long noteId) {
        return ResponseEntity.ok(commandService.delete(noteId));
    }
}
