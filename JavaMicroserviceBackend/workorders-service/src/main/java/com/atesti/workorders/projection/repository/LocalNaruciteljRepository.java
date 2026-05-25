package com.atesti.workorders.projection.repository;

import com.atesti.workorders.projection.model.LocalNarucitelj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalNaruciteljRepository extends JpaRepository<LocalNarucitelj, Long> {
}
