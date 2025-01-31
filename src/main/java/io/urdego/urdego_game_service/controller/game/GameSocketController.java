package io.urdego.urdego_game_service.controller.game;

import io.urdego.urdego_game_service.controller.game.dto.request.ScoreReq;
import io.urdego.urdego_game_service.controller.game.dto.response.GameEndRes;
import io.urdego.urdego_game_service.controller.game.dto.response.ScoreRes;
import io.urdego.urdego_game_service.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameSocketController {

    private final GameService gameService;

    // 점수 요청
//    @MessageMapping("/game/score")
//    public void giveScores(ScoreReq request) {
//        ScoreRes response = gameService.giveScores(request);
//        messagingTemplate.convertAndSend("/game-service/sub/" + response.roomId(), response);
//    }

    // 게임 종료
//    @MessageMapping("/game/end")
//    public void endGame(String gameId) {
//        GameEndRes response = gameService.finishGame(gameId);
//        messagingTemplate.convertAndSend("/game-service/sub/" + response.roomId(), response);
//    }
}
