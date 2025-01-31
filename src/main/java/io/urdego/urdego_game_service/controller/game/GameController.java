package io.urdego.urdego_game_service.controller.game;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.urdego.urdego_game_service.controller.game.dto.request.GameCreateReq;
import io.urdego.urdego_game_service.controller.game.dto.request.ScoreReq;
import io.urdego.urdego_game_service.controller.game.dto.response.GameCreateRes;
import io.urdego.urdego_game_service.controller.game.dto.response.GameEndRes;
import io.urdego.urdego_game_service.controller.game.dto.response.ScoreRes;
import io.urdego.urdego_game_service.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game-service/game")
public class GameController {

    private final GameService gameService;

    @Tag(name = "게임 API")
    @Operation(summary = "게임 생성", description = "roomId로 게임 생성")
    @PostMapping("/start")
    public ResponseEntity<GameCreateRes> startGame(@RequestBody GameCreateReq request) {
        GameCreateRes response = gameService.createGame(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Tag(name = "게임 Socket")
    @Operation(summary = "게임 점수", description = "게임 점수 조회")
    @PostMapping("/score")
    public ResponseEntity<ScoreRes> giveScores(@RequestBody ScoreReq request) {
        ScoreRes response = gameService.giveScores(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "게임 Socket")
    @Operation(summary = "게임 종료", description = "게임 종료")
    @PostMapping("/end")
    public ResponseEntity<GameEndRes> endGame(@RequestBody String gameId) {
        GameEndRes response = gameService.finishGame(gameId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
