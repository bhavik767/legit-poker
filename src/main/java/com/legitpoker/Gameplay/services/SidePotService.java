package com.legitpoker.Gameplay.services;

import com.legitpoker.Gameplay.model.SidePot;
import com.legitpoker.Gameplay.repositories.SidePotRepository;
import com.legitpoker.model.Player;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SidePotService {

    private final SidePotRepository sidePotRepository;

    /**
     * Calculates side pots from list of players' currentBet values.
     * Expects players to have currentBet >= 0 and includes players who folded (folded players with nonzero bet are still
     * eligible for side pots only if they were eligible when they put chips in — but standard is that folded players are not eligible at showdown).
     *
     * Implementation:
     * - Sort players by currentBet ascending.
     * - For each distinct contribution level, create a pot that includes all remaining players.
     * - Persist side pots for tableId; delete previous.
     */
    @Transactional
    public void calculateSidePots(String tableId, List<Player> players) {
        sidePotRepository.deleteByTableId(tableId);

        // Only players who contributed >0 are considered when computing pots.
        List<Player> contributors = players.stream()
                .filter(p -> p.getCurrentBet() > 0)
                .sorted(Comparator.comparingInt(Player::getCurrentBet))
                .collect(Collectors.toList());

        if (contributors.isEmpty()) return;

        int previous = 0;
        List<Player> remaining = new ArrayList<>(contributors);

        for (Player p : contributors) {
            int level = p.getCurrentBet();
            int contribution = level - previous;
            if (contribution > 0 && !remaining.isEmpty()) {
                int potAmount = contribution * remaining.size();
                String eligibleIds = remaining.stream()
                        .map(pl -> String.valueOf(pl.getId()))
                        .collect(Collectors.joining(","));
                SidePot sp = SidePot.builder()
                        .tableId(tableId)
                        .amount(potAmount)
                        .eligiblePlayerIds(eligibleIds)
                        .build();
                sidePotRepository.save(sp);
                previous = level;
            }
            // remove this player from remaining for next iterations
            remaining.remove(p);
        }
    }

    public List<SidePot> findByTable(String tableId) {
        return sidePotRepository.findByTableId(tableId);
    }

    @Transactional
    public void clearSidePots(String tableId) {
        sidePotRepository.deleteByTableId(tableId);
    }
}
