package com.legitpoker.repository;

import com.legitpoker.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    int countByTableId(String tableId);
    boolean existsByTableIdAndNickname(String tableId, String nickname);
    boolean existsByTableIdAndSeatNumber(String tableId, int seatNumber);
    List<Player> findByTableId(String tableId);
}
