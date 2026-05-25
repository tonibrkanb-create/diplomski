package com.atesti.repository;

import com.atesti.entity.Aktivnost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AktivnostRepository extends JpaRepository<Aktivnost, Long> {
    List<Aktivnost> findByIsActiveTrueOrderByIdAsc();
    List<Aktivnost> findByIdInAndIsActiveTrue(List<Long> ids);
}
