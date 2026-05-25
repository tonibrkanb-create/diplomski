package com.atesti.clients.domain.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "narucitelji")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Narucitelj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String adresa;
    private String mjesto;
    private String postanskiBroj;
    private String drzava;

    @Column(name = "OIB")
    private String OIB;

    private String ziroRacun;

    @Column(columnDefinition = "TEXT")
    private String ostalo;

    private String kontaktOsoba;
    private String telefon;
    private String mobitel;
    private String fax;
    private String email;

    @Column(nullable = false)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public String formattedAddress() {
        return Stream.of(adresa, postanskiBroj, mjesto, drzava)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(", "));
    }

    public void updateFrom(String name, String adresa, String mjesto, String postanskiBroj,
                           String drzava, String oib, String ziroRacun, String ostalo,
                           String kontaktOsoba, String telefon, String mobitel, String fax,
                           String email, String location, String comment) {
        if (name != null) this.name = name;
        if (adresa != null) this.adresa = adresa;
        if (mjesto != null) this.mjesto = mjesto;
        if (postanskiBroj != null) this.postanskiBroj = postanskiBroj;
        if (drzava != null) this.drzava = drzava;
        if (oib != null) this.OIB = oib;
        if (ziroRacun != null) this.ziroRacun = ziroRacun;
        if (ostalo != null) this.ostalo = ostalo;
        if (kontaktOsoba != null) this.kontaktOsoba = kontaktOsoba;
        if (telefon != null) this.telefon = telefon;
        if (mobitel != null) this.mobitel = mobitel;
        if (fax != null) this.fax = fax;
        if (email != null) this.email = email;
        if (location != null) this.location = location;
        if (comment != null) this.comment = comment;
    }
}
