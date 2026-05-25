package com.atesti.portal.projection.repository;

import com.atesti.portal.projection.model.LocalRadniNalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRadniNalogRepository extends JpaRepository<LocalRadniNalog, Long> {
}
