package com.atesti.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "radni_nalozi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RadniNalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String brojNaloga;

    @Column(nullable = false)
    private LocalDateTime datum;

    @Column(nullable = false)
    private String objekt;

    @Builder.Default
    private Boolean fakturirano = false;

    @Builder.Default
    private Boolean zavrseno = false;

    @Column(columnDefinition = "TEXT")
    private String opis;

    private String brojPonude;
    private String brojRacuna;
    private String narudzbenica;
    private String ugovor;

    @Column(columnDefinition = "TEXT")
    private String aktivnosti;

    private String pdfUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "narucitelj_id")
    @JsonBackReference
    private Narucitelj narucitelj;

    @Column(name = "narucitelj_id", insertable = false, updatable = false)
    private Long naruciteljId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @Column(name = "assigned_user_id", insertable = false, updatable = false)
    private Long assignedUserId;

    @OneToMany(mappedBy = "radniNalog", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("radniNalog-documents")
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "radniNalog", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("radniNalog-notes")
    @Builder.Default
    private List<Note> notes = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
