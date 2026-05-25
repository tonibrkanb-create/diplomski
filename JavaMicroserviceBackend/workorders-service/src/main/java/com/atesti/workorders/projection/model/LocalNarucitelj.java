package com.atesti.workorders.projection.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "local_narucitelj")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalNarucitelj {

    @Id
    private Long id;

    private String name;
    private String adresa;
    private String mjesto;
    private String postanskiBroj;
    private String drzava;

    @Column(name = "oib")
    private String OIB;

    private String ziroRacun;

    @Column(columnDefinition = "TEXT")
    private String ostalo;

    private String kontaktOsoba;
    private String telefon;
    private String mobitel;
    private String fax;
    private String email;
    private String location;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
