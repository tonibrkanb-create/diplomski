package com.atesti.clients.domain.repository;

import com.atesti.clients.domain.model.Narucitelj;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NaruciteljRepository extends JpaRepository<Narucitelj, Long>, JpaSpecificationExecutor<Narucitelj> {


    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Narucitelj> findById(Long id);

}
