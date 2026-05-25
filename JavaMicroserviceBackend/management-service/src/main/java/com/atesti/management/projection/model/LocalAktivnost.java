package com.atesti.management.projection.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "local_aktivnost")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalAktivnost {

    @Id
    private Long id;

    private String aktivnost;

    @Column(precision = 10, scale = 2)
    private BigDecimal cijena;

    private Integer rokTrajanja;
    private Boolean isActive;
}
