package com.atesti.workorders.projection.model;

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

    private BigDecimal cijena;

    private Integer rokTrajanja;

    @Builder.Default
    private Boolean isActive = true;
}
