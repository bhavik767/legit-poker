package com.legitpoker.comms.dto.audio;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class AudioParticipantView {
    private String nickname;
    private boolean muted;
    private Instant joinedAt;
}
