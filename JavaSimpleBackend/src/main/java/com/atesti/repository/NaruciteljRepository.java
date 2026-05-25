package com.atesti.repository;

import com.atesti.entity.Narucitelj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NaruciteljRepository extends JpaRepository<Narucitelj, Long>, JpaSpecificationExecutor<Narucitelj> {
}
