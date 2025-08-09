package com.legitpoker.controller;

import com.legitpoker.dto.PlayerActionRequest;
import com.legitpoker.dto.PlayerActionResponse;
import com.legitpoker.service.GameplayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gameplay")
public class GameplayController {

    private final GameplayService gameplayService;

    public GameplayController(GameplayService gameplayService) {
        this.gameplayService = gameplayService;
    }

    @PostMapping("/action")
    public ResponseEntity<PlayerActionResponse> performAction(
            @Valid @RequestBody PlayerActionRequest request) {

        PlayerActionResponse response = gameplayService.performPlayerAction(request);
        return ResponseEntity.ok(response);
    }
}
