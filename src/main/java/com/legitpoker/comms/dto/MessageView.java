package com.legitpoker.comms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.Instant;

@Getter @AllArgsConstructor
public class MessageView {
    private Long id;
    private String nickname;
    private String text;
    private Instant createdAt;
}
