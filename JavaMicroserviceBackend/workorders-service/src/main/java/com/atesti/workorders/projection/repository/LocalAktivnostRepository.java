package com.atesti.workorders.projection.repository;

import com.atesti.workorders.projection.model.LocalAktivnost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalAktivnostRepository extends JpaRepository<LocalAktivnost, Long> {
    List<LocalAktivnost> findByIdInAndIsActiveTrue(List<Long> ids);
}
