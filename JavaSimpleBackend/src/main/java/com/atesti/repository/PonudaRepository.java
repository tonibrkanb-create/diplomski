package com.atesti.repository;

import com.atesti.entity.Ponuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PonudaRepository extends JpaRepository<Ponuda, Long> {
    List<Ponuda> findByKorisnikIdOrderByCreatedAtDesc(Long korisnikId);
    Optional<Ponuda> findByIdAndKorisnikId(Long id, Long korisnikId);
    List<Ponuda> findAllByOrderByCreatedAtDesc();
}
