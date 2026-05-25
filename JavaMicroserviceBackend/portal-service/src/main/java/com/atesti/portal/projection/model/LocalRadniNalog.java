package com.atesti.portal.projection.model;

import jakarta.persistence.*;
import lombok.*;

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
}
