package io.urdego.urdego_game_service.controller.game;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.urdego.urdego_game_service.controller.game.dto.request.GameCreateReq;
import io.urdego.urdego_game_service.controller.game.dto.response.GameCreateRes;
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
@RequestMapping("/api/game-service")
public class GameController {

    private final GameService gameService;

    // 게임 생성
    @Tag(name = "게임 API")
    @Operation(summary = "게임 생성", description = "roomId로 게임 생성")
    @PostMapping("/game/start")
    public ResponseEntity<GameCreateRes> startGame(@RequestBody GameCreateReq request) {
        GameCreateRes response = gameService.createGame(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
