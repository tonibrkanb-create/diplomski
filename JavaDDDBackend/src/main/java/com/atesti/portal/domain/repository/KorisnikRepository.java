package com.atesti.portal.domain.repository;

import com.atesti.portal.domain.model.Korisnik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KorisnikRepository extends JpaRepository<Korisnik, Long> {
    Optional<Korisnik> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Korisnik> findByIsActiveTrueOrderByImeAsc();
}
