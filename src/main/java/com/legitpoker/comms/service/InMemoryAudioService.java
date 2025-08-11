package com.legitpoker.comms.service;

import com.legitpoker.comms.api.AudioChatFacade;
import com.legitpoker.comms.dto.audio.*;
import com.legitpoker.exception.NotFoundException;
import com.legitpoker.repository.PokerTableRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryAudioService implements AudioChatFacade {

    private final PokerTableRepository tables;
    private final SimpMessagingTemplate broker;

    public InMemoryAudioService(PokerTableRepository tables, SimpMessagingTemplate broker) {
        this.tables = tables;
        this.broker = broker;
    }

    static final class Participant {
        final String nickname;
        volatile boolean muted = false;
        final String sessionId;
        final Instant joinedAt = Instant.now();
        Participant(String nickname, String sessionId) { this.nickname = nickname; this.sessionId = sessionId; }
    }

    static final class Room {
        final Map<String, Participant> byNick = new ConcurrentHashMap<>();     // nick -> participant
        final Map<String, String> bySession = new ConcurrentHashMap<>();       // sessionId -> nick
    }

    // tableId -> room
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    private Room room(String code) {
        if (!tables.existsById(code)) throw new NotFoundException("Table not found");
        return rooms.computeIfAbsent(code, k -> new Room());
    }

    @Override
    public void join(String code, String nickname, String sessionId) {
        Room r = room(code);
        r.byNick.put(nickname, new Participant(nickname, sessionId));
        r.bySession.put(sessionId, nickname);
        broadcastParticipants(code, r);
        // also notify JOIN
        var msg = new AudioSignalMessage();
        msg.setType(SignalType.JOIN); msg.setFrom(nickname);
        broker.convertAndSend(topic(code), msg);
    }

    @Override
    public void leave(String code, String nickname, String sessionId) {
        Room r = room(code);
        r.byNick.remove(nickname);
        r.bySession.values().removeIf(n -> Objects.equals(n, nickname));
        broadcastParticipants(code, r);
        var msg = new AudioSignalMessage();
        msg.setType(SignalType.LEAVE); msg.setFrom(nickname);
        broker.convertAndSend(topic(code), msg);
    }

    @Override
    public void setMute(String code, String nickname, boolean muted) {
        Room r = room(code);
        var p = r.byNick.get(nickname);
        if (p != null) {
            p.muted = muted;
            var msg = new AudioSignalMessage();
            msg.setType(muted ? SignalType.MUTE : SignalType.UNMUTE);
            msg.setFrom(nickname); msg.setMuted(muted);
            broker.convertAndSend(topic(code), msg);
            broadcastParticipants(code, r);
        }
    }

    @Override
    public void relay(String code, AudioSignalMessage msg) {
        // Just fan-out to the room; clients filter by 'to' when needed
        broker.convertAndSend(topic(code), msg);
    }

    @Override
    public AudioRoomSnapshot snapshot(String code) {
        Room r = room(code);
        var list = r.byNick.values().stream()
                .sorted(Comparator.comparing(p -> p.joinedAt))
                .map(p -> new AudioParticipantView(p.nickname, p.muted, p.joinedAt))
                .toList();
        return new AudioRoomSnapshot(code, list);
    }

    @Override
    public void evictBySession(String sessionId) {
        // find and remove from whichever room they were in
        for (var entry : rooms.entrySet()) {
            var r = entry.getValue();
            var nick = r.bySession.remove(sessionId);
            if (nick != null) {
                r.byNick.remove(nick);
                broadcastParticipants(entry.getKey(), r);
                var msg = new AudioSignalMessage();
                msg.setType(SignalType.LEAVE); msg.setFrom(nick);
                broker.convertAndSend(topic(entry.getKey()), msg);
                break;
            }
        }
    }

    private void broadcastParticipants(String code, Room r) {
        var snap = snapshot(code);
        var msg = new AudioSignalMessage();
        msg.setType(SignalType.PARTICIPANTS);
        // We piggyback the snapshot via a separate topic message (best practice is a distinct endpoint;
        // but to keep client simpler, send both: summary + dedicated REST below)
        broker.convertAndSend(topic(code), msg);
        // Also send dedicated snapshot
        broker.convertAndSend(topic(code) + "/participants", snap);
    }

    private String topic(String code) { return "/topic/audio/" + code; }
}
