package com.atesti.management.projection.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "local_sustav_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalSustavLog {

    @Id
    private Long id;

    private String action;
    private String entity;
    private Long entityId;
    private Long userId;
    private String userName;

    @Column(columnDefinition = "TEXT")
    private String details;

    private LocalDateTime createdAt;
}
