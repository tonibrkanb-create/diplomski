package com.atesti.workorders.application.dto.query;

import com.atesti.clients.application.dto.query.NaruciteljResponse;
import com.atesti.staffidentity.application.dto.query.UserResponse;
import com.atesti.workorders.domain.model.RadniNalog;
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
    private LocalDateTime datum;
    private String objekt;
    private Boolean fakturirano;
    private Boolean zavrseno;
    private String opis;
    private String brojPonude;
    private String brojRacuna;
    private String narudzbenica;
    private String ugovor;
    private String aktivnosti;
    private String pdfUrl;
    private NaruciteljResponse narucitelj;
    private Long naruciteljId;
    private UserResponse assignedUser;
    private Long assignedUserId;
    private List<DocumentResponse> documents;
    private List<NoteResponse> notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RadniNalogResponse from(RadniNalog entity) {
        return RadniNalogResponse.builder()
                .id(entity.getId())
                .brojNaloga(entity.getBrojNaloga())
                .datum(entity.getDatum())
                .objekt(entity.getObjekt())
                .fakturirano(entity.getFakturirano())
                .zavrseno(entity.getZavrseno())
                .opis(entity.getOpis())
                .brojPonude(entity.getBrojPonude())
                .brojRacuna(entity.getBrojRacuna())
                .narudzbenica(entity.getNarudzbenica())
                .ugovor(entity.getUgovor())
                .aktivnosti(entity.getAktivnosti())
                .pdfUrl(entity.getPdfUrl())
                .narucitelj(entity.getNarucitelj() != null ? NaruciteljResponse.from(entity.getNarucitelj()) : null)
                .naruciteljId(entity.getNaruciteljId())
                .assignedUser(entity.getAssignedUser() != null ? UserResponse.from(entity.getAssignedUser()) : null)
                .assignedUserId(entity.getAssignedUserId())
                .documents(entity.getDocuments() != null
                        ? entity.getDocuments().stream().map(DocumentResponse::from).collect(Collectors.toList())
                        : null)
                .notes(entity.getNotes() != null
                        ? entity.getNotes().stream().map(NoteResponse::from).collect(Collectors.toList())
                        : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
