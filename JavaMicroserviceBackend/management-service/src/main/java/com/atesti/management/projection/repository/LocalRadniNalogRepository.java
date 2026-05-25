package com.atesti.management.projection.repository;

import com.atesti.management.projection.model.LocalRadniNalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalRadniNalogRepository extends JpaRepository<LocalRadniNalog, Long> {
    long countByFakturirano(Boolean fakturirano);
    long countByZavrseno(Boolean zavrseno);
    List<LocalRadniNalog> findByAssignedUserIdIsNotNull();
    List<LocalRadniNalog> findByAssignedUserIdOrderByDatumDesc(Long assignedUserId);
}
