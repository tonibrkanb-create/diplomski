package com.atesti.workorders.application.dto.query;

import com.atesti.clients.application.dto.query.NaruciteljResponse;
import com.atesti.staffidentity.application.dto.query.UserResponse;
import com.atesti.workorders.domain.model.RadniNalogProjection;
import com.atesti.workorders.domain.persistance.RadniNalogEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadniNalogResponse {
    private Long id;
    private String brojNaloga;
    private Boolean fakturirano;
    private Boolean zavrseno;
    private Long naruciteljId;

    public static RadniNalogResponse from(RadniNalogProjection entity) {
        return RadniNalogResponse.builder()
                .id(entity.getId())
                .brojNaloga(entity.getBrojNaloga())
                .fakturirano(Boolean.valueOf(entity.getFakturirano()))
                .zavrseno(Boolean.valueOf(entity.getZavrseno()))
                .naruciteljId(entity.getNaruciteljId())
                .build();
    }
}
