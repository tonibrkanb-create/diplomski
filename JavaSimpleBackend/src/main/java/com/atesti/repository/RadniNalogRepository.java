package com.atesti.repository;

import com.atesti.entity.RadniNalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RadniNalogRepository extends JpaRepository<RadniNalog, Long> {
    List<RadniNalog> findByNaruciteljId(Long naruciteljId);
    Optional<RadniNalog> findByBrojNaloga(String brojNaloga);
    List<RadniNalog> findByAssignedUserIdOrderByDatumDesc(Long assignedUserId);
    List<RadniNalog> findByAssignedUserIdIsNotNull();
    long countByFakturirano(Boolean fakturirano);
    long countByZavrseno(Boolean zavrseno);
}
