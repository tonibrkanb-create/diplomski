package com.atesti.catalogue.domain.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "aktivnosti")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aktivnost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aktivnost;

    @Column(nullable = false)
    private Integer rokTrajanja;

    @Column(precision = 10, scale = 2)
    private BigDecimal cijena;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void updateDetails(String aktivnost, Integer rokTrajanja, BigDecimal cijena) {
        if (aktivnost != null) this.aktivnost = aktivnost;
        if (rokTrajanja != null) this.rokTrajanja = rokTrajanja;
        if (cijena != null) this.cijena = cijena;
    }

    public void deactivate() {
        if (!this.isActive) throw new IllegalStateException("Aktivnost is already inactive");
        this.isActive = false;
    }

    public LocalDate computeExpiryDate(LocalDate serviceDate) {
        return serviceDate.plusMonths(this.rokTrajanja);
    }

    public static Aktivnost create(String aktivnost, Integer rokTrajanja, BigDecimal cijena) {
        if (aktivnost == null || rokTrajanja == null) {
            throw new IllegalArgumentException("Error creating aktivnost: aktivnost and rokTrajanja are required");
        }
        return Aktivnost.builder()
                .aktivnost(aktivnost)
                .rokTrajanja(rokTrajanja)
                .cijena(cijena)
                .isActive(true)
                .build();
    }
}
