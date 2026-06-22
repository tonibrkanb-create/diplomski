package com.atesti.workorders.domain.model;

import com.atesti.clients.domain.model.Narucitelj;
import com.atesti.workorders.domain.persistance.RadniNalogEntity;
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

    @Column(nullable = false)
    private String aktivnost;

    @Column(nullable = false)
    private LocalDate datumIsteka;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "narucitelj_id", insertable = false, updatable = false)
    private Narucitelj narucitelj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "radni_nalog_id", insertable = false, updatable = false)
    private RadniNalogEntity radniNalogEntity;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
