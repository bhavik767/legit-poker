package com.legitpoker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateTableSettingsRequest {
    @NotNull @Min(1) private Integer smallBlind;
    @NotNull @Min(1) private Integer bigBlind;
    @NotNull @Min(1) private Integer startingStack;
    @NotNull @Min(5) private Integer turnTimerSeconds;
    private boolean rabbitHunting;
    private boolean runItTwice;

    @AssertTrue(message = "bigBlind must be greater than smallBlind")
    public boolean isBigBlindGreater() {
        return bigBlind != null && smallBlind != null && bigBlind > smallBlind;
    }
}
