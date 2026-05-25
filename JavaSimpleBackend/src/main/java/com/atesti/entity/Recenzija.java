package com.atesti.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recenzije")
@Getter
@Setter
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
}
