package com.legitpoker.service;

import com.legitpoker.dto.CreateTableRequest;
import com.legitpoker.dto.CreateTableResponse;
import com.legitpoker.model.PokerTable;
import com.legitpoker.repository.PokerTableRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PokerTableService {

    private final PokerTableRepository repository;
    private static final String ALPHABET = "abcdefghjkmnpqrstuvwxyz23456789";
    private final SecureRandom rng = new SecureRandom();

    private final String baseUrl;
    private final int codeLength;

    public PokerTableService(
            PokerTableRepository repository,
            @Value("${app.base-url}") String baseUrl,
            @Value("${app.table-code-length:8}") int codeLength
    ) {
        this.repository = repository;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.codeLength = codeLength;
    }

    public CreateTableResponse createTable(CreateTableRequest req) {
        var table = new PokerTable();
        table.setId(generateUniqueCode());

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
                shareUrl
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

    private String randomCode(int len) {
        var sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHABET.charAt(rng.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
