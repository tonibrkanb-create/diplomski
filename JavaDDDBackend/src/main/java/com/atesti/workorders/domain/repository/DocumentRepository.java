package com.atesti.workorders.domain.repository;

import com.atesti.workorders.domain.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByRadniNalogId(Long radniNalogId);
    Optional<Document> findByIdAndRadniNalogId(Long id, Long radniNalogId);
}
