package com.atesti.management.projection.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "local_radni_nalog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalRadniNalog {

    @Id
    private Long id;

    private String brojNaloga;
    private Long naruciteljId;
    private Long assignedUserId;

    @Column(columnDefinition = "TEXT")
    private String aktivnosti;

    @Builder.Default
    private Boolean zavrseno = false;

    @Builder.Default
    private Boolean fakturirano = false;

    private LocalDateTime datum;

    public String deriveStatus() {
        if (Boolean.TRUE.equals(fakturirano)) return "fakturiran";
        if (Boolean.TRUE.equals(zavrseno)) return "zavrsen";
        return "aktivan";
    }
}
