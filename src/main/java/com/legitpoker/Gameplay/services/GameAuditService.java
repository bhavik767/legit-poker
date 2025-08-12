package com.legitpoker.Gameplay.services;

import com.legitpoker.Gameplay.model.GameAudit;
import com.legitpoker.Gameplay.repositories.GameAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class GameAuditService {

    private final GameAuditRepository repo;

    public void log(String tableId, String msg) {
        GameAudit audit = new GameAudit();
        audit.setTableId(tableId);
        audit.setEventType("INFO"); // Or pass as parameter if you want dynamic event types
        audit.setEventData(msg);
        audit.setCreatedAt(Instant.now());
        repo.save(audit);
    }
}
