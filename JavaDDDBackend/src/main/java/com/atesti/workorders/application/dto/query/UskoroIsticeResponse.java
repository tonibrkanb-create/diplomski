package com.atesti.workorders.application.dto.query;

import com.atesti.workorders.domain.model.UskoroIstice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UskoroIsticeResponse {
    private Long id;
    private String narucitelj;
    private String radniNalog;
    private String aktivnost;
    private LocalDate datumIsteka;
    private Boolean isActive;

    public static UskoroIsticeResponse from(UskoroIstice entity) {
        return UskoroIsticeResponse.builder()
                .id(entity.getId())
                .narucitelj(entity.getNarucitelj() != null ? entity.getNarucitelj().getName() : null)
                .radniNalog(entity.getRadniNalog() != null ? entity.getRadniNalog().getBrojNaloga() : null)
                .aktivnost(entity.getAktivnost())
                .datumIsteka(entity.getDatumIsteka())
                .isActive(entity.getIsActive())
                .build();
    }
}
