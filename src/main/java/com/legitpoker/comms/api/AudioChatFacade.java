package com.legitpoker.comms.api;

import com.legitpoker.comms.dto.audio.*;

public interface AudioChatFacade {
    void join(String tableCode, String nickname, String sessionId);
    void leave(String tableCode, String nickname, String sessionId);
    void setMute(String tableCode, String nickname, boolean muted);
    void relay(String tableCode, AudioSignalMessage msg); // OFFER/ANSWER/ICE broadcast
    AudioRoomSnapshot snapshot(String tableCode);
    void evictBySession(String sessionId); // cleanup on WS disconnect
}
