package com.legitpoker.Gameplay.repositories;

import com.legitpoker.Gameplay.model.SidePot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SidePotRepository extends JpaRepository<SidePot, Long> {
    List<SidePot> findByTableId(String tableId);
    void deleteByTableId(String tableId);
}
