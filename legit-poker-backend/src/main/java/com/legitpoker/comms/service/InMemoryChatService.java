package com.legitpoker.comms.service;

import com.legitpoker.comms.api.CommsFacade;
import com.legitpoker.comms.dto.GetMessagesResponse;
import com.legitpoker.comms.dto.MessageView;
import com.legitpoker.comms.dto.SendMessageRequest;
import com.legitpoker.exception.ConflictException;
import com.legitpoker.exception.NotFoundException;
import com.legitpoker.repository.PokerTableRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InMemoryChatService implements CommsFacade {

    private static final int MAX_MESSAGES_PER_TABLE = 200;

    private final PokerTableRepository tables;

    public InMemoryChatService(PokerTableRepository tables) {
        this.tables = tables;
    }

    // tableId -> room (in-memory only)
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    static final class Room {
        final Deque<MessageView> buffer = new ConcurrentLinkedDeque<>();
        final AtomicLong seq = new AtomicLong(0);
    }

    @Override
    public MessageView send(String code, SendMessageRequest req) {
        ensureTable(code);

        String nick = req.getNickname() == null ? "" : req.getNickname().trim();
        String text = sanitize(req.getText());

        if (nick.length() < 2 || nick.length() > 20) {
            throw new ConflictException("Invalid nickname");
        }

        Room room = rooms.computeIfAbsent(code, k -> new Room());
        long id = room.seq.incrementAndGet();

        MessageView msg = new MessageView(id, nick, text, Instant.now());
        room.buffer.addLast(msg);

        // cap buffer
        while (room.buffer.size() > MAX_MESSAGES_PER_TABLE) {
            room.buffer.pollFirst();
        }
        return msg;
    }

    @Override
    public GetMessagesResponse list(String code, String requestingNick, Long sinceId) {
        ensureTable(code);
        Room room = rooms.computeIfAbsent(code, k -> new Room());

        List<MessageView> out = new ArrayList<>();
        Long last = sinceId;
        for (MessageView mv : room.buffer) {
            if (sinceId == null || mv.getId() > sinceId) {
                out.add(mv);
                if (last == null || mv.getId() > last) last = mv.getId();
            }
        }
        return new GetMessagesResponse(out, last);
    }

    private void ensureTable(String code) {
        if (!tables.existsById(code)) throw new NotFoundException("Table not found");
    }

    private String sanitize(String input) {
        String s = input == null ? "" : input.strip();
        if (s.isEmpty()) throw new ConflictException("Empty message");
        if (s.length() > 500) s = s.substring(0, 500);
        return s.replaceAll("\\s+", " ");
    }
}
