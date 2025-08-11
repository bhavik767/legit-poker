package com.legitpoker.comms.controller;

import com.legitpoker.comms.api.AudioChatFacade;
import com.legitpoker.comms.dto.audio.AudioRoomSnapshot;
import com.legitpoker.comms.dto.audio.AudioSignalMessage;
import com.legitpoker.comms.dto.audio.SignalType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioWsController {

    private final AudioChatFacade audio;

    public AudioWsController(AudioChatFacade audio) {
        this.audio = audio;
    }

    // WebSocket/STOMP: clients send to /app/audio/{code}
    @MessageMapping("/audio/{code}")
    public void handle(@DestinationVariable String code,
                       @Payload AudioSignalMessage msg,
                       SimpMessageHeaderAccessor headers) {
        String sessionId = headers.getSessionId();

        if (msg.getType() == SignalType.JOIN) {
            audio.join(code, msg.getFrom(), sessionId);
        } else if (msg.getType() == SignalType.LEAVE) {
            audio.leave(code, msg.getFrom(), sessionId);
        } else if (msg.getType() == SignalType.MUTE || msg.getType() == SignalType.UNMUTE) {
            audio.setMute(code, msg.getFrom(), msg.getType() == SignalType.MUTE);
        } else {
            // OFFER / ANSWER / ICE
            audio.relay(code, msg);
        }
    }

    // Simple REST helper to fetch current participants (useful on page load)
    @GetMapping("/tables/{code}/audio/participants")
    public AudioRoomSnapshot participants(@PathVariable @NotBlank String code) {
        return audio.snapshot(code);
    }
}
