package io.urdego.urdego_game_service.controller.game;

import io.urdego.urdego_game_service.controller.game.dto.response.GameEndRes;
import io.urdego.urdego_game_service.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    // 게임 종료
    @MessageMapping("/game/end")
    public void endGame(String gameId) {
        GameEndRes response = gameService.finishGame(gameId);
        messagingTemplate.convertAndSend("/game-service/sub/" + response.gameId(), response);
    }
}
