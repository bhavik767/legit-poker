package com.legitpoker.comms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SendMessageRequest {
    @NotBlank @Size(min=2, max=20) private String nickname;
    @NotBlank @Size(min=1, max=500) private String text;
}
