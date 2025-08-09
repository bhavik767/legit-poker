package com.legitpoker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "player",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_player_table_seat", columnNames = {"table_id","seat_number"}),
                @UniqueConstraint(name = "uq_player_table_nickname", columnNames = {"table_id","nickname"})
        }
)
@Getter @Setter @NoArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_id", nullable = false, length = 32)
    private String tableId;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @Column(nullable = false)
    private int chips;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
