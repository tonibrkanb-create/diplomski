package com.atesti.workorders.application.dto.query;

import com.atesti.workorders.domain.model.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponse {
    private Long id;
    private LocalDateTime date;
    private String text;
    private Long radniNalogId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NoteResponse from(Note entity) {
        return NoteResponse.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .text(entity.getText())
                .radniNalogId(entity.getRadniNalogId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
