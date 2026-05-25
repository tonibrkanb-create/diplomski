package com.atesti.workorders.application.dto.query;

import com.atesti.workorders.domain.model.RadniNalog;
import com.atesti.workorders.projection.model.LocalNarucitelj;
import com.atesti.workorders.projection.model.LocalUser;
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
    private NaruciteljInfo narucitelj;
    private Long naruciteljId;
    private UserInfo assignedUser;
    private Long assignedUserId;
    private List<DocumentResponse> documents;
    private List<NoteResponse> notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NaruciteljInfo {
        private Long id;
        private String name;
        private String adresa;
        private String mjesto;
        private String postanskiBroj;
        private String drzava;
        private String OIB;
        private String ziroRacun;
        private String ostalo;
        private String kontaktOsoba;
        private String telefon;
        private String mobitel;
        private String fax;
        private String email;
        private String location;
        private String comment;

        public static NaruciteljInfo from(LocalNarucitelj n) {
            if (n == null) return null;
            return NaruciteljInfo.builder()
                    .id(n.getId())
                    .name(n.getName())
                    .adresa(n.getAdresa())
                    .mjesto(n.getMjesto())
                    .postanskiBroj(n.getPostanskiBroj())
                    .drzava(n.getDrzava())
                    .OIB(n.getOIB())
                    .ziroRacun(n.getZiroRacun())
                    .ostalo(n.getOstalo())
                    .kontaktOsoba(n.getKontaktOsoba())
                    .telefon(n.getTelefon())
                    .mobitel(n.getMobitel())
                    .fax(n.getFax())
                    .email(n.getEmail())
                    .location(n.getLocation())
                    .comment(n.getComment())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String ime;
        private String prezime;

        public static UserInfo from(LocalUser u) {
            if (u == null) return null;
            return UserInfo.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .ime(u.getIme())
                    .prezime(u.getPrezime())
                    .build();
        }
    }

    public static RadniNalogResponse from(RadniNalog entity, LocalNarucitelj narucitelj, LocalUser user) {
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
                .narucitelj(NaruciteljInfo.from(narucitelj))
                .naruciteljId(entity.getNaruciteljId())
                .assignedUser(UserInfo.from(user))
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
