package com.atesti.clients.domain.repository;

import com.atesti.clients.domain.model.Narucitelj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NaruciteljRepository extends JpaRepository<Narucitelj, Long>, JpaSpecificationExecutor<Narucitelj> {
}
