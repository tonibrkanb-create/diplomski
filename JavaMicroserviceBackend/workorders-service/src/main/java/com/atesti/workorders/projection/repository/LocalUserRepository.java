package com.atesti.workorders.projection.repository;

import com.atesti.workorders.projection.model.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalUserRepository extends JpaRepository<LocalUser, Long> {
}
