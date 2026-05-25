package com.atesti.portal.domain.model;

import com.atesti.workorders.domain.model.RadniNalog;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "radni_nalog_id")
    private RadniNalog radniNalog;

    @Column(name = "radni_nalog_id", insertable = false, updatable = false)
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

    public static Recenzija create(Korisnik korisnik, Integer ocjena, String komentar, RadniNalog radniNalog) {
        if (ocjena == null || ocjena < 1 || ocjena > 5) {
            throw new IllegalArgumentException("Ocjena mora biti između 1 i 5");
        }
        return Recenzija.builder()
                .korisnik(korisnik)
                .ocjena(ocjena)
                .komentar(komentar)
                .radniNalog(radniNalog)
                .build();
    }

    public void respond(String odgovor) {
        this.odgovor = odgovor;
    }
}
