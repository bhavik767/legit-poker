package com.legitpoker.Gameplay.controller;

import com.legitpoker.Gameplay.facade.GameplayFacade;
import com.legitpoker.Gameplay.dto.PlayerActionRequest;
import com.legitpoker.Gameplay.dto.PlayerActionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gameplay")
@RequiredArgsConstructor
public class GameplayController {

    private final GameplayFacade gameplayFacade;

    @PostMapping("/action")
    public ResponseEntity<PlayerActionResponse> playerAction(
            @RequestBody @Valid PlayerActionRequest request) {
        return ResponseEntity.ok(gameplayFacade.handlePlayerAction(request));
    }
}

