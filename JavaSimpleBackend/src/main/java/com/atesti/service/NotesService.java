package com.atesti.service;

import com.atesti.dto.NoteRequest;
import com.atesti.entity.Note;
import com.atesti.entity.RadniNalog;
import com.atesti.exception.BadRequestException;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.NoteRepository;
import com.atesti.repository.RadniNalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotesService {

    private final NoteRepository noteRepository;
    private final RadniNalogRepository radniNalogRepository;

    public List<Note> getNotesByRadniNalog(Long radniNalogId) {
        return noteRepository.findByRadniNalogIdOrderByDateDesc(radniNalogId);
    }

    public Note addNote(Long radniNalogId, NoteRequest request) {
        RadniNalog nalog = radniNalogRepository.findById(radniNalogId)
                .orElseThrow(() -> new BadRequestException("Error adding note: Radni nalog not found"));

        if (request.getText() == null || request.getText().isBlank()) {
            throw new BadRequestException("Error adding note: text is required");
        }

        LocalDateTime date = LocalDateTime.now();
        if (request.getDate() != null && !request.getDate().isBlank()) {
            try {
                date = LocalDateTime.parse(request.getDate());
            } catch (Exception e) {
                try {
                    date = LocalDate.parse(request.getDate()).atStartOfDay();
                } catch (Exception e2) {
                    date = LocalDateTime.now();
                }
            }
        }

        Note note = Note.builder()
                .date(date)
                .text(request.getText())
                .radniNalog(nalog)
                .build();

        return noteRepository.save(note);
    }

    public Note deleteNote(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        noteRepository.delete(note);
        return note;
    }
}
