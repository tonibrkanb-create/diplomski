package com.atesti.identity.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    private String ime;
    private String prezime;
    private String email;

    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void deactivate() {
        this.isActive = false;
    }

    public String displayName() {
        String full = ((ime != null ? ime : "") + " " + (prezime != null ? prezime : "")).trim();
        return full.isEmpty() ? username : full;
    }

    public void updateDetails(String username, String ime, String prezime, String email) {
        if (username != null) this.username = username;
        if (ime != null) this.ime = ime;
        if (prezime != null) this.prezime = prezime;
        if (email != null) this.email = email;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
