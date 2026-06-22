package com.atesti.workorders.domain.persistance;

import com.atesti.clients.domain.model.Narucitelj;
import com.atesti.staffidentity.domain.model.User;
import com.atesti.workorders.domain.model.Document;
import com.atesti.workorders.domain.model.Note;
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
public class RadniNalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String brojNaloga;

    @Column(nullable = false)
    private LocalDateTime datum;

    @Column(nullable = false)
    private String objekt;

    private Boolean fakturirano;

    private Boolean zavrseno;

    @Column(columnDefinition = "TEXT")
    private String opis;

    private String brojPonude;
    private String brojRacuna;
    private String narudzbenica;
    private String ugovor;

    @Column(columnDefinition = "TEXT")
    private String aktivnosti;

    private String pdfUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "narucitelj_id")
    private Narucitelj narucitelj;

    @Column(name = "narucitelj_id", insertable = false, updatable = false)
    private Long naruciteljId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @Column(name = "assigned_user_id", insertable = false, updatable = false)
    private Long assignedUserId;

    @OneToMany(mappedBy = "radniNalog", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "radniNalog", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Note> notes = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public RadniNalogEntity(
            String brojNaloga,
            String datum,
            String objekt,
            Boolean fakturirano,
            Boolean zavrseno,
            String opis,
            String brojPonude,
            String brojRacuna,
            String narudzbenica,
            String ugovor,
            String aktivnosti,
            String pdfUrl,
            Narucitelj narucitelj,
            User assignedUser
    ) {
        validate(brojNaloga, datum, objekt);

        this.brojNaloga = brojNaloga;
        this.datum = LocalDateTime.parse(datum);
        this.objekt = objekt;
        this.fakturirano = fakturirano;
        this.zavrseno = zavrseno;
        this.opis = opis;
        this.brojPonude = brojPonude;
        this.brojRacuna = brojRacuna;
        this.narudzbenica = narudzbenica;
        this.ugovor = ugovor;
        this.aktivnosti = aktivnosti;
        this.pdfUrl = pdfUrl;
        this.narucitelj = narucitelj;
        this.assignedUser = assignedUser;

        this.documents = new ArrayList<>();
        this.notes = new ArrayList<>();
    }

    public void update(
            String brojNaloga,
            LocalDateTime datum,
            String objekt,
            Boolean fakturirano,
            Boolean zavrseno,
            String opis,
            String brojPonude,
            String brojRacuna,
            String narudzbenica,
            String ugovor,
            String pdfUrl
    ) {
        validate(
                brojNaloga != null ? brojNaloga : this.brojNaloga,
                datum != null ? datum.toString() : this.datum.toString(),
                objekt != null ? objekt : this.objekt
        );

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

    public void assignTo(User user) {
        this.assignedUser = user;
    }


    public String deriveStatus() {
        if (Boolean.TRUE.equals(fakturirano)) return "fakturiran";
        if (Boolean.TRUE.equals(zavrseno)) return "zavrsen";
        return "aktivan";
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

    public void setNarucitelj(Narucitelj narucitelj) {
        this.narucitelj = narucitelj;
    }

    private void validate(
            String brojNaloga,
            String datum,
            String objekt
    ) {
        if (brojNaloga == null || brojNaloga.isBlank()) {
            throw new IllegalArgumentException("brojNaloga must not be empty");
        }

        if (datum == null) {
            throw new IllegalArgumentException("datum must not be null");
        }

        if (objekt == null || objekt.isBlank()) {
            throw new IllegalArgumentException("objekt must not be empty");
        }
    }
}
