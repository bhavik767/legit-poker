package com.legitpoker.Gameplay.repositories;

import com.legitpoker.Gameplay.model.GameAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameAuditRepository extends JpaRepository<GameAudit, Long> {
    List<GameAudit> findByTableIdOrderByCreatedAtAsc(String tableId);
}
