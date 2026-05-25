package com.atesti.workorders.domain.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "radni_nalozi")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RadniNalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String brojNaloga;

    @Column(nullable = false)
    private LocalDateTime datum;

    @Column(nullable = false)
    private String objekt;

    @Builder.Default
    private Boolean fakturirano = false;

    @Builder.Default
    private Boolean zavrseno = false;

    @Column(columnDefinition = "TEXT")
    private String opis;

    private String brojPonude;
    private String brojRacuna;
    private String narudzbenica;
    private String ugovor;

    @Column(columnDefinition = "TEXT")
    private String aktivnosti;

    private String pdfUrl;

    @Column(name = "narucitelj_id")
    @Setter
    private Long naruciteljId;

    @Column(name = "assigned_user_id")
    private Long assignedUserId;

    @OneToMany(mappedBy = "radniNalog", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "radniNalog", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Note> notes = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void assignTo(Long userId) {
        this.assignedUserId = userId;
    }

    public void complete() {
        this.zavrseno = true;
    }

    public void invoice() {
        this.fakturirano = true;
        this.zavrseno = true;
    }

    public String deriveStatus() {
        if (Boolean.TRUE.equals(fakturirano)) return "fakturiran";
        if (Boolean.TRUE.equals(zavrseno)) return "zavrsen";
        return "aktivan";
    }

    public List<Long> getAktivnostiIds(ObjectMapper mapper) {
        if (aktivnosti == null || aktivnosti.isBlank()) return List.of();
        try {
            return mapper.readValue(aktivnosti, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public void setAktivnostiIds(List<Long> ids, ObjectMapper mapper) {
        try {
            this.aktivnosti = mapper.writeValueAsString(ids);
        } catch (JsonProcessingException e) {
            this.aktivnosti = "[]";
        }
    }

    public void updateDetails(String brojNaloga, LocalDateTime datum, String objekt,
                              Boolean fakturirano, Boolean zavrseno, String opis,
                              String brojPonude, String brojRacuna, String narudzbenica,
                              String ugovor, String pdfUrl) {
        if (brojNaloga != null) this.brojNaloga = brojNaloga;
        if (datum != null) this.datum = datum;
        if (objekt != null) this.objekt = objekt;
        if (fakturirano != null) this.fakturirano = fakturirano;
        if (zavrseno != null) this.zavrseno = zavrseno;
        if (opis != null) this.opis = opis;
        if (brojPonude != null) this.brojPonude = brojPonude;
        if (brojRacuna != null) this.brojRacuna = brojRacuna;
        if (narudzbenica != null) this.narudzbenica = narudzbenica;
        if (ugovor != null) this.ugovor = ugovor;
        if (pdfUrl != null) this.pdfUrl = pdfUrl;
    }
}
