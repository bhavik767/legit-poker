package com.legitpoker.comms.dto.audio;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AudioSignalMessage {
    private SignalType type;   // JOIN, OFFER, ANSWER, ICE, LEAVE, MUTE, UNMUTE
    private String from;       // sender nickname
    private String to;         // optional target nickname (for OFFER/ANSWER/ICE)
    private String sdp;        // SDP (offer/answer)
    private String candidate;  // ICE candidate JSON/string
    private Boolean muted;     // for MUTE/UNMUTE events
}
