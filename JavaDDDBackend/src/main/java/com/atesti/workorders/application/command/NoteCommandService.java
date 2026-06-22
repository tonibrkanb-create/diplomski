package com.atesti.workorders.application.command;

import com.atesti.shared.exception.BadRequestException;
import com.atesti.shared.exception.ResourceNotFoundException;
import com.atesti.workorders.application.dto.command.AddNoteCommand;
import com.atesti.workorders.application.dto.query.NoteResponse;
import com.atesti.workorders.domain.model.Note;
import com.atesti.workorders.domain.persistance.RadniNalogEntity;
import com.atesti.workorders.domain.repository.NoteRepository;
import com.atesti.workorders.domain.repository.RadniNalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoteCommandService {

    private final NoteRepository noteRepository;
    private final RadniNalogRepository radniNalogRepository;

    @Transactional
    public NoteResponse add(Long radniNalogId, AddNoteCommand command) {
        RadniNalogEntity nalog = radniNalogRepository.findById(radniNalogId)
                .orElseThrow(() -> new BadRequestException("Error adding note: Radni nalog not found"));

        if (command.getText() == null || command.getText().isBlank()) {
            throw new BadRequestException("Error adding note: text is required");
        }

        LocalDateTime date = LocalDateTime.now();
        if (command.getDate() != null && !command.getDate().isBlank()) {
            try {
                date = LocalDateTime.parse(command.getDate());
            } catch (Exception e) {
                try {
                    date = LocalDate.parse(command.getDate()).atStartOfDay();
                } catch (Exception e2) {
                    date = LocalDateTime.now();
                }
            }
        }

        Note note = Note.builder()
                .date(date)
                .text(command.getText())
                .radniNalogEntity(nalog)
                .build();

        Note saved = noteRepository.save(note);
        return NoteResponse.from(saved);
    }

    @Transactional
    public NoteResponse delete(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        NoteResponse response = NoteResponse.from(note);
        noteRepository.delete(note);
        return response;
    }
}
