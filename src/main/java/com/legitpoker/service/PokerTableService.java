package com.legitpoker.service;

import com.legitpoker.dto.*;
import com.legitpoker.exception.ConflictException;
import com.legitpoker.exception.ForbiddenException;
import com.legitpoker.exception.NotFoundException;
import com.legitpoker.model.Player;
import com.legitpoker.model.PokerTable;
import com.legitpoker.repository.PlayerRepository;
import com.legitpoker.repository.PokerTableRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Comparator;

@Service
public class PokerTableService {

    private final PokerTableRepository repository;
    private final PlayerRepository playerRepo;
    private static final String ALPHABET = "abcdefghjkmnpqrstuvwxyz23456789";
    private final SecureRandom rng = new SecureRandom();

    private final String baseUrl;
    private final int codeLength;

    public PokerTableService(
            PokerTableRepository repository,
            PlayerRepository playerRepo,
            @Value("${app.base-url}") String baseUrl,
            @Value("${app.table-code-length:8}") int codeLength
    ) {
        this.repository = repository;
        this.playerRepo = playerRepo;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.codeLength = codeLength;
    }

    public CreateTableResponse createTable(CreateTableRequest req) {
        var table = new PokerTable();
        table.setId(generateUniqueCode());
        table.setOwnerToken(generateOwnerToken());
        table.setSmallBlind(req.getSmallBlind());
        table.setBigBlind(req.getBigBlind());
        table.setStartingStack(req.getStartingStack());
        table.setTurnTimerSeconds(req.getTurnTimerSeconds());
        table.setRabbitHunting(req.isRabbitHunting());
        table.setRunItTwice(req.isRunItTwice());

        var saved = repository.save(table);

        String shareUrl = baseUrl + "/join/" + saved.getId();
        return new CreateTableResponse(
                saved.getId(),
                saved.getVariant(),
                saved.getSmallBlind(),
                saved.getBigBlind(),
                saved.getStartingStack(),
                saved.getTurnTimerSeconds(),
                saved.isRabbitHunting(),
                saved.isRunItTwice(),
                shareUrl,
                saved.getOwnerToken()

        );
    }

    public JoinTableResponse joinTable(String code, JoinTableRequest req) {
        PokerTable table = repository.findById(code)
                .orElseThrow(() -> new NotFoundException("Table not found"));

        // Capacity check
        int count = playerRepo.countByTableId(code);
        if (count >= 10) throw new ConflictException("Table is full");

        // Nickname checks
        String nickname = req.getNickname().trim();
        if (nickname.isEmpty() || nickname.length() < 2 || nickname.length() > 20) {
            throw new ConflictException("Invalid nickname");
        }
        if (playerRepo.existsByTableIdAndNickname(code, nickname)) {
            throw new ConflictException("Nickname already seated");
        }

        // Seat selection: first free seat [0..9]
        boolean[] used = new boolean[10];
        for (var p : playerRepo.findByTableId(code)) {
            int s = p.getSeatNumber();
            if (s >= 0 && s < 10) used[s] = true;
        }
        int seat = -1;
        for (int i = 0; i < 10; i++) if (!used[i]) { seat = i; break; }
        if (seat == -1) throw new ConflictException("Table is full");

        // Persist player
        Player player = new Player();
        player.setTableId(code);
        player.setNickname(nickname);
        player.setSeatNumber(seat);
        player.setChips(table.getStartingStack());
        playerRepo.save(player);

        // Snapshot
        var players = playerRepo.findByTableId(code).stream()
                .sorted(Comparator.comparingInt(Player::getSeatNumber))
                .map(p -> new JoinTableResponse.PlayerView(p.getSeatNumber(), p.getNickname(), p.getChips()))
                .toList();

        return new JoinTableResponse(
                code,
                seat,
                player.getChips(),
                players,
                new JoinTableResponse.Blinds(table.getSmallBlind(), table.getBigBlind()),
                table.getTurnTimerSeconds(),
                new JoinTableResponse.Options(table.isRabbitHunting(), table.isRunItTwice())
        );
    }

    public CreateTableResponse updateTableSettings(String code, String ownerToken, UpdateTableSettingsRequest req) {
        var table = repository.findById(code).orElseThrow(() -> new NotFoundException("Table not found"));
        if (ownerToken == null || !ownerToken.equals(table.getOwnerToken())) {
            throw new ForbiddenException("Only the table owner can edit settings");
        }

        table.setSmallBlind(req.getSmallBlind());
        table.setBigBlind(req.getBigBlind());
        table.setStartingStack(req.getStartingStack());
        table.setTurnTimerSeconds(req.getTurnTimerSeconds());
        table.setRabbitHunting(req.isRabbitHunting());
        table.setRunItTwice(req.isRunItTwice());

        var saved = repository.save(table);
        String shareUrl = baseUrl + "/join/" + saved.getId();

        return new CreateTableResponse(
                saved.getId(),
                saved.getVariant(),
                saved.getSmallBlind(),
                saved.getBigBlind(),
                saved.getStartingStack(),
                saved.getTurnTimerSeconds(),
                saved.isRabbitHunting(),
                saved.isRunItTwice(),
                shareUrl,
                null   // don’t re-send ownerToken after creation
        );
    }


    private String generateUniqueCode() {
        // Try a few times to avoid extremely rare collisions
        for (int attempt = 0; attempt < 5; attempt++) {
            String code = randomCode(codeLength);
            if (!repository.existsById(code)) return code;
        }
        String code;
        do {
            code = randomCode(codeLength + 2);
        } while (repository.existsById(code));
        return code;
    }

    private String generateOwnerToken() {
        // 32-char base32-ish token (no ambiguous chars)
        final String ALPHABET = "abcdefghjkmnpqrstuvwxyz23456789";
        var sb = new StringBuilder(32);
        for (int i = 0; i < 32; i++) sb.append(ALPHABET.charAt(rng.nextInt(ALPHABET.length())));
        return sb.toString();
    }

    private String randomCode(int len) {
        var sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHABET.charAt(rng.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
