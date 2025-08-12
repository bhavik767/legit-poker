package com.legitpoker.Gameplay.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "game_audit")
@Getter
@Setter
@NoArgsConstructor
public class GameAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String tableId;

    @Column(nullable = false, length = 50)
    private String eventType; // e.g., "BET", "POT_CREATED", "WIN", "SIDE_POT"

    @Lob
    private String eventData; // JSON or stringified details

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
