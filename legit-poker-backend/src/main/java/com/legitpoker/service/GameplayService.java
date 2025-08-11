package com.legitpoker.service;

import com.legitpoker.dto.PlayerActionRequest;
import com.legitpoker.dto.PlayerActionResponse;
import com.legitpoker.exception.ConflictException;
import com.legitpoker.exception.ForbiddenException;
import com.legitpoker.exception.NotFoundException;
import com.legitpoker.model.Player;
import com.legitpoker.model.PokerTable;
import com.legitpoker.repository.PlayerRepository;
import com.legitpoker.repository.PokerTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GameplayService {

    private final PokerTableRepository pokerTableRepository;
    private final PlayerRepository playerRepository;

    // This map keeps track of table state in-memory (turns, pot, bets)
    private final Map<String, TableState> tableStates = new HashMap<>();

    public GameplayService(PokerTableRepository pokerTableRepository, PlayerRepository playerRepository) {
        this.pokerTableRepository = pokerTableRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public PlayerActionResponse performPlayerAction(PlayerActionRequest request) {
        PokerTable table = pokerTableRepository.findById(request.getTableId())
                .orElseThrow(() -> new NotFoundException("Table not found"));

        Player player = playerRepository.findById(Long.valueOf(request.getPlayerId()))
                .orElseThrow(() -> new NotFoundException("Player not found"));

        // Check if player is part of this table
        boolean isPlayerAtTable = playerRepository.findByTableId(request.getTableId())
                .stream()
                .anyMatch(p -> p.getId().equals(Long.valueOf(request.getPlayerId())));

        if (!isPlayerAtTable) {
            throw new ForbiddenException("Player is not part of this table");
        }

        // Load table state (or initialize if first time)
        TableState state = tableStates.computeIfAbsent(table.getId(), t -> initTableState(table));

        // Validate it's the player's turn
        Long currentTurnPlayerId = state.turnOrder.get(state.currentTurnIndex);
        if (!Objects.equals(currentTurnPlayerId, player.getId())) {
            throw new ConflictException("It's not your turn");
        }

        // Action handling
        switch (request.getAction().toUpperCase()) {
            case "CHECK":
                handleCheck(player, state);
                break;

            case "CALL":
                handleCall(player, state);
                break;

            case "RAISE":
                handleRaise(player, (int) request.getRaiseAmount(), state);
                break;

            case "FOLD":
                handleFold(player, state);
                break;

            case "ALL_IN":
                handleAllIn(player, state);
                break;

            default:
                throw new ConflictException("Invalid action");
        }

        // Advance turn
        advanceTurn(state);

        // Persist player chip counts
        playerRepository.saveAll(state.players);

        // Persist table (if pot or blinds changed)
        pokerTableRepository.save(table);

        return new PlayerActionResponse("SUCCESS",
                "Player " + player.getNickname() + " performed action: " + request.getAction());
    }

    private TableState initTableState(PokerTable table) {
        List<Player> players = playerRepository.findByTableId(table.getId());
        players.sort(Comparator.comparingInt(Player::getSeatNumber)); // order by seat

        TableState state = new TableState();
        state.players = players;
        state.turnOrder = players.stream().map(Player::getId).toList();
        state.currentTurnIndex = 0;
        state.pot = 0;
        state.bets = new HashMap<>();
        players.forEach(p -> state.bets.put(p.getId(), 0));
        return state;
    }

    private void handleCheck(Player player, TableState state) {
        int highestBet = Collections.max(state.bets.values());
        if (state.bets.get(player.getId()) < highestBet) {
            throw new ConflictException("You cannot check when there is a higher bet");
        }
    }

    private void handleCall(Player player, TableState state) {
        int highestBet = Collections.max(state.bets.values());
        int toCall = highestBet - state.bets.get(player.getId());
        if (player.getChips() < toCall) {
            throw new ConflictException("Not enough chips to call");
        }
        player.setChips(player.getChips() - toCall);
        state.bets.put(player.getId(), highestBet);
        state.pot += toCall;
    }

    private void handleRaise(Player player, int raiseAmount, TableState state) {
        if (raiseAmount <= 0) {
            throw new ConflictException("Raise amount must be positive");
        }
        int highestBet = Collections.max(state.bets.values());
        int toCall = highestBet - state.bets.get(player.getId());
        int totalBet = toCall + raiseAmount;
        if (player.getChips() < totalBet) {
            throw new ConflictException("Not enough chips to raise");
        }
        player.setChips(player.getChips() - totalBet);
        state.bets.put(player.getId(), highestBet + raiseAmount);
        state.pot += totalBet;
    }

    private void handleFold(Player player, TableState state) {
        // Remove player from turn order
        state.turnOrder = state.turnOrder.stream()
                .filter(id -> !id.equals(player.getId()))
                .toList();
        state.bets.remove(player.getId());
    }

    private void handleAllIn(Player player, TableState state) {
        int allInAmount = player.getChips();
        int highestBet = Collections.max(state.bets.values());
        int toCall = highestBet - state.bets.get(player.getId());
        int totalBet = Math.min(allInAmount, toCall + allInAmount);

        player.setChips(0);
        state.bets.put(player.getId(), state.bets.get(player.getId()) + totalBet);
        state.pot += totalBet;
    }

    private void advanceTurn(TableState state) {
        if (state.turnOrder.isEmpty()) return;
        state.currentTurnIndex = (state.currentTurnIndex + 1) % state.turnOrder.size();
    }

    private static class TableState {
        List<Player> players;
        List<Long> turnOrder;
        int currentTurnIndex;
        int pot;
        Map<Long, Integer> bets;
    }
}
