package com.legitpoker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JoinTableResponse {
    private String tableId;
    private int seatNumber;
    private int chips;
    private List<PlayerView> players;
    private Blinds blinds;
    private int turnTimerSeconds;
    private Options options;

    @Getter @AllArgsConstructor
    public static class PlayerView {
        private int seat;
        private String nickname;
        private int chips;
    }

    @Getter @AllArgsConstructor
    public static class Blinds {
        private int small;
        private int big;
    }

    @Getter @AllArgsConstructor
    public static class Options {
        private boolean rabbitHunting;
        private boolean runItTwice;
    }
}
