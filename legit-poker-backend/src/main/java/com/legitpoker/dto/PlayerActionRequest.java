package com.legitpoker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerActionRequest {

    @NotBlank
    private String tableId;

    @NotBlank
    private String playerId;

    @NotBlank
    private String action; // "CHECK", "CALL", "RAISE", "FOLD", "ALL_IN"

    @Min(0)
    private long raiseAmount; // only used for RAISE


}
