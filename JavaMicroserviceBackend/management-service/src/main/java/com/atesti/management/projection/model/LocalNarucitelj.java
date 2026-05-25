package com.atesti.management.projection.model;

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

    @Column(name = "OIB")
    private String OIB;

    private String kontaktOsoba;
    private String telefon;
    private String email;
}
