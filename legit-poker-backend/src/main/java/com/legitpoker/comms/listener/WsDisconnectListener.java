package com.legitpoker.comms.listener;

import com.legitpoker.comms.api.AudioChatFacade;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WsDisconnectListener {

    private final AudioChatFacade audio;

    public WsDisconnectListener(AudioChatFacade audio) { this.audio = audio; }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent e) {
        audio.evictBySession(e.getSessionId());
    }
}
