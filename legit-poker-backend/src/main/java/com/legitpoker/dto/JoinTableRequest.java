package com.legitpoker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JoinTableRequest {
    @NotBlank
    @Size(min = 2, max = 20)
    private String nickname;
}
