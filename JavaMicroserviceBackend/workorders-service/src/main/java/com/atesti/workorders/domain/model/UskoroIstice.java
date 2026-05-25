package com.atesti.workorders.domain.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "uskoro_istice")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UskoroIstice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "narucitelj_id", nullable = false)
    private Long naruciteljId;

    @Column(name = "radni_nalog_id", nullable = false)
    private Long radniNalogId;

    @Column(name = "aktivnost_id")
    private Long aktivnostId;

    @Column(nullable = false)
    private String aktivnost;

    private String naruciteljName;

    private String radniNalogBrojNaloga;

    private String aktivnostName;

    @Column(nullable = false)
    private LocalDate datumIsteka;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
