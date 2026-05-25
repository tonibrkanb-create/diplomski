package com.atesti.workorders.application.dto.query;

import com.atesti.workorders.domain.model.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Long id;
    private String name;
    private String url;
    private Long radniNalogId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DocumentResponse from(Document entity) {
        return DocumentResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .url(entity.getUrl())
                .radniNalogId(entity.getRadniNalogId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
