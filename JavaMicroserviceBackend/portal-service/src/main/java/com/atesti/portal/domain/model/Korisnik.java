package com.atesti.portal.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "korisnici")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Korisnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ime;

    @Column(nullable = false)
    private String prezime;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefon;
    private String tvrtka;
    private String adresa;
    private String mjesto;
    private String postanskiBroj;
    private String drzava;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void updateProfile(String ime, String prezime, String telefon, String tvrtka,
                              String adresa, String mjesto, String postanskiBroj, String drzava) {
        if (ime != null) this.ime = ime;
        if (prezime != null) this.prezime = prezime;
        if (telefon != null) this.telefon = telefon;
        if (tvrtka != null) this.tvrtka = tvrtka;
        if (adresa != null) this.adresa = adresa;
        if (mjesto != null) this.mjesto = mjesto;
        if (postanskiBroj != null) this.postanskiBroj = postanskiBroj;
        if (drzava != null) this.drzava = drzava;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
