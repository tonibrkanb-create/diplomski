package com.atesti.identity.domain.repository;

import com.atesti.identity.domain.model.SustavLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SustavLogRepository extends JpaRepository<SustavLog, Long> {

    @Query("SELECT s FROM SustavLog s WHERE " +
           "(:entity IS NULL OR s.entity = :entity) AND " +
           "(:action IS NULL OR s.action = :action) AND " +
           "(:fromDate IS NULL OR s.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR s.createdAt <= :toDate) " +
           "ORDER BY s.createdAt DESC")
    List<SustavLog> findFiltered(
            @Param("entity") String entity,
            @Param("action") String action,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}
