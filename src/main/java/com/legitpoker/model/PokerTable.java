package com.legitpoker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "poker_table")
@Data
@NoArgsConstructor
public class PokerTable {
    @Id
    private String id;

    private String variant = "Texas Hold'em";
    private int smallBlind;
    private int bigBlind;
    private int startingStack;
    private int turnTimerSeconds;
    private boolean rabbitHunting;
    private boolean runItTwice;

    @Column(nullable = false, length = 64)
    private String ownerToken;

    // NEW: current pot (single total pot before side pots are computed)
    @Column(nullable = false)
    private int pot = 0;

    // NEW: whether a hand is currently in progress
    private boolean handInProgress = false;

    // helper omitted because we don't map players here (use PlayerRepository)
}
