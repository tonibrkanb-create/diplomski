package com.atesti.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "obavijesti")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Obavijest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "korisnik_id")
    private Korisnik korisnik;

    @Column(name = "korisnik_id", insertable = false, updatable = false)
    private Long korisnikId;

    @Column(nullable = false)
    private String naslov;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String poruka;

    @Builder.Default
    private Boolean procitana = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
