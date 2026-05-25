package com.atesti.clients.projection.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "local_radni_nalog_count")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalRadniNalogCount {

    @Id
    private Long naruciteljId;

    @Builder.Default
    private Long count = 0L;
}
