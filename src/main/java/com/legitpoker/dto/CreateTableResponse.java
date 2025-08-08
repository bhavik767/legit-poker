package com.legitpoker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTableResponse {
    private String id;
    private String variant;
    private int smallBlind;
    private int bigBlind;
    private int startingStack;
    private int turnTimerSeconds;
    private boolean rabbitHunting;
    private boolean runItTwice;
    private String shareUrl;
}
