package com.atesti.catalogue.application.dto.query;

import com.atesti.catalogue.domain.model.Aktivnost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AktivnostResponse {
    private Long id;
    private String aktivnost;
    private Integer rokTrajanja;
    private BigDecimal cijena;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AktivnostResponse from(Aktivnost entity) {
        return AktivnostResponse.builder()
                .id(entity.getId())
                .aktivnost(entity.getAktivnost())
                .rokTrajanja(entity.getRokTrajanja())
                .cijena(entity.getCijena())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
