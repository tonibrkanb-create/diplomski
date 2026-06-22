package com.atesti.workorders.domain.repository;

import com.atesti.workorders.domain.persistance.RadniNalogEntity;
import com.atesti.workorders.domain.model.RadniNalogProjection;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RadniNalogRepository extends RadniNalogReadRepository, RadniNalogWriteRepository, JpaRepository<RadniNalogEntity, Long> {


    default RadniNalogEntity save(RadniNalogEntity entity) { return saveAndFlush(entity) ;}

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RadniNalogEntity> findById(long radniNalogId);

    @Query("""
    SELECT new com.atesti.workorders.domain.model.RadniNalogProjection(rn.id, rn.brojNaloga, rn.naruciteljId, rn.fakturirano, rn.zavrseno)
    FROM RadniNalog rn
""")
    List<RadniNalogProjection> findAllRadniNalog();

    @Query("""
    SELECT new com.atesti.workorders.domain.model.RadniNalogProjection(
        rn.id,
        rn.brojNaloga,
        rn.datum,
        rn.objekt,
        rn.fakturirano,
        rn.zavrseno,
        rn.opis,
        rn.brojPonude,
        rn.brojRacuna,
        rn.narudzbenica,
        rn.ugovor,
        rn.aktivnosti,
        rn.naruciteljId,
        rn.assignedUserId
    )
    FROM RadniNalog rn
    WHERE rn.id = :radniNalogId
""")
    Optional<RadniNalogProjection> findRadniNalog(Long radniNalogId);

    @Query("""
    SELECT new com.atesti.workorders.domain.model.RadniNalogProjection(rn.id, rn.brojNaloga, rn.naruciteljId, rn.fakturirano, rn.zavrseno)
    FROM RadniNalog rn
    WHERE rn.naruciteljId = :naruciteljId
""")
    List<RadniNalogProjection> findByNaruciteljId(Long naruciteljId);

    long countByFakturirano(Boolean fakturirano);

    long countByZavrseno(Boolean zavrseno);

    List<RadniNalogEntity> findByAssignedUserIdOrderByDatumDesc(Long assignedUserId);
    List<RadniNalogEntity> findByAssignedUserIdIsNotNull();
    Optional<RadniNalogEntity> findByBrojNaloga(String brojNaloga);
}
