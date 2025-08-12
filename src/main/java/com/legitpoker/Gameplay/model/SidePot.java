package com.legitpoker.Gameplay.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "side_pot")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SidePot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_id", nullable = false, length = 32)
    private String tableId;

    @Column(nullable = false)
    private int amount;

    // comma-separated eligible player ids (string) — easy to persist; service converts as needed
    @Column(name = "eligible_player_ids", nullable = false, length = 1024)
    private String eligiblePlayerIds;
}
