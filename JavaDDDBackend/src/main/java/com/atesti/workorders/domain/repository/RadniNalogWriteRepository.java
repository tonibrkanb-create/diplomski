package com.atesti.workorders.domain.repository;

import com.atesti.workorders.domain.persistance.RadniNalogEntity;

import java.util.Optional;

public interface RadniNalogWriteRepository {

    RadniNalogEntity save(RadniNalogEntity entity);

    Optional<RadniNalogEntity> findById(long radniNalogId);

    void delete(long radniNalogId);
}
