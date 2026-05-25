package com.atesti.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "narucitelji")
@Getter
@Setter
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

    @OneToMany(mappedBy = "narucitelj", fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<RadniNalog> radniNalozi = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
