package com.legitpoker.comms.dto.audio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class AudioRoomSnapshot {
    private String tableId;
    private List<AudioParticipantView> participants;
}
