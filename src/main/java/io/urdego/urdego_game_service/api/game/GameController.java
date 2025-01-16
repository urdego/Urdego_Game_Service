package io.urdego.urdego_game_service.api.game;

import io.urdego.urdego_game_service.api.game.dto.request.GameCreateReq;
import io.urdego.urdego_game_service.api.game.dto.response.GameCreateRes;
import io.urdego.urdego_game_service.api.game.dto.response.GameEndRes;
import io.urdego.urdego_game_service.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    // 게임 생성
    @MessageMapping("/game/start")
    public void startGame(GameCreateReq request) {
        GameCreateRes response = gameService.createGame(request);
        messagingTemplate.convertAndSend("/game-service/sub" + response.gameId(), response);
    }

    // 게임 종료
    @MessageMapping("/game/end")
    public void endGame(String gameId) {
        GameEndRes response = gameService.finishGame(gameId);
        messagingTemplate.convertAndSend("/game-service/sub" + response.gameId(), response);
    }
}
