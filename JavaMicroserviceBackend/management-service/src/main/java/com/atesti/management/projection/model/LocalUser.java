package com.atesti.management.projection.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "local_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalUser {

    @Id
    private Long id;

    private String username;
    private String ime;
    private String prezime;
    private Boolean isActive;
}
