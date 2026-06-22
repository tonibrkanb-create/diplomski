package com.atesti.workorders.domain.repository;

import com.atesti.workorders.domain.model.RadniNalogProjection;

import java.util.List;
import java.util.Optional;

public interface RadniNalogReadRepository {

    List<RadniNalogProjection> findAllRadniNalog();

    Optional<RadniNalogProjection> findRadniNalog(int radniNalogId);

}
