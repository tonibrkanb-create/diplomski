package com.atesti.management.projection.repository;

import com.atesti.management.projection.model.LocalNarucitelj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalNaruciteljRepository extends JpaRepository<LocalNarucitelj, Long> {
}
