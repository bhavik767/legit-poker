package com.legitpoker.Gameplay.facade;

import com.legitpoker.Gameplay.dto.PlayerActionRequest;
import com.legitpoker.Gameplay.dto.PlayerActionResponse;
import com.legitpoker.Gameplay.services.GameplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameplayFacade {

    private final GameplayService gameplayService;
//    private final GameplayValidator gameplayValidator;

    public PlayerActionResponse handlePlayerAction(PlayerActionRequest request) {
//        gameplayValidator.validate(request);
        return gameplayService.performPlayerAction(request);
    }
}

