package com.atesti.portal.domain.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "ponude")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ponuda {

    private static final Set<String> VALID_STATUSES = Set.of("nova", "poslana", "odobrena", "odbijena");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "korisnik_id")
    private Korisnik korisnik;

    @Column(name = "korisnik_id", insertable = false, updatable = false)
    private Long korisnikId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String opis;

    private String vrstaAtesta;
    private String lokacija;
    private LocalDate zeljeniDatum;

    @Builder.Default
    private String status = "nova";

    @Column(columnDefinition = "TEXT")
    private String odgovor;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void updateStatus(String status, String odgovor) {
        if (status != null) {
            if (!VALID_STATUSES.contains(status)) {
                throw new IllegalArgumentException("Nevazeci status: " + status);
            }
            this.status = status;
        }
        if (odgovor != null) {
            this.odgovor = odgovor;
        }
    }
}
