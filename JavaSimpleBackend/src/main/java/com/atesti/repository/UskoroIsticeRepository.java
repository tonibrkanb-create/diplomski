package com.atesti.repository;

import com.atesti.entity.UskoroIstice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UskoroIsticeRepository extends JpaRepository<UskoroIstice, Long> {
    List<UskoroIstice> findByIsActiveTrueAndDatumIstekaBetweenOrderByDatumIstekaAsc(LocalDate from, LocalDate to);
    void deleteByRadniNalogId(Long radniNalogId);
}
