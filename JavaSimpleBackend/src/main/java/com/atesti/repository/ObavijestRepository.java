package com.atesti.repository;

import com.atesti.entity.Obavijest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ObavijestRepository extends JpaRepository<Obavijest, Long> {
    List<Obavijest> findByKorisnikIdOrderByCreatedAtDesc(Long korisnikId);
    Optional<Obavijest> findByIdAndKorisnikId(Long id, Long korisnikId);
}
