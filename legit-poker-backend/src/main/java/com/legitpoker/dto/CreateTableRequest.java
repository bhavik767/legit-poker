package com.legitpoker.dto;

import lombok.Data;

@Data
public class CreateTableRequest {
    private int smallBlind;
    private int bigBlind;
    private int startingStack;
    private int turnTimerSeconds;
    private boolean rabbitHunting;
    private boolean runItTwice;
}
