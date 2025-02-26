package com.task.management.system.repository;

import com.task.management.system.model.entity.DeactivatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeactivatedTokenRepository extends JpaRepository<DeactivatedToken, UUID> {
}
