package com.atesti.portal.domain.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recenzije")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recenzija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "korisnik_id")
    private Korisnik korisnik;

    @Column(name = "korisnik_id", insertable = false, updatable = false)
    private Long korisnikId;

    @Column(name = "radni_nalog_id")
    private Long radniNalogId;

    @Column(nullable = false)
    private Integer ocjena;

    @Column(columnDefinition = "TEXT")
    private String komentar;

    @Column(columnDefinition = "TEXT")
    private String odgovor;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static Recenzija create(Korisnik korisnik, Integer ocjena, String komentar, Long radniNalogId) {
        if (ocjena == null || ocjena < 1 || ocjena > 5) {
            throw new IllegalArgumentException("Ocjena mora biti izmedju 1 i 5");
        }
        return Recenzija.builder()
                .korisnik(korisnik)
                .ocjena(ocjena)
                .komentar(komentar)
                .radniNalogId(radniNalogId)
                .build();
    }

    public void respond(String odgovor) {
        this.odgovor = odgovor;
    }
}
