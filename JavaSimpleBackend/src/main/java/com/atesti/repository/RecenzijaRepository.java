package com.atesti.repository;

import com.atesti.entity.Recenzija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecenzijaRepository extends JpaRepository<Recenzija, Long> {
    List<Recenzija> findByKorisnikIdOrderByCreatedAtDesc(Long korisnikId);
    Optional<Recenzija> findByIdAndKorisnikId(Long id, Long korisnikId);
    List<Recenzija> findAllByOrderByCreatedAtDesc();
}
