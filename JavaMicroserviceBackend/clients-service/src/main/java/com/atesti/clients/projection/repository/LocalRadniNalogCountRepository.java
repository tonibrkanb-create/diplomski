package com.atesti.clients.projection.repository;

import com.atesti.clients.projection.model.LocalRadniNalogCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRadniNalogCountRepository extends JpaRepository<LocalRadniNalogCount, Long> {
}
