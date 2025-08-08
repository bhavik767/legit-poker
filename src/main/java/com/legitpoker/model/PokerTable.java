package com.legitpoker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
