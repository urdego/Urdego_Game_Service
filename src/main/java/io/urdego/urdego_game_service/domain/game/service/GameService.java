package io.urdego.urdego_game_service.domain.game.service;

import io.urdego.urdego_game_service.api.game.dto.request.GameCreateReq;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.game.entity.Game;

public interface GameService {
    // 게임 생성
    Game createGame(GameCreateReq request);

    // 게임 상태 변경
    Game updateGameStatusById(String gameId, Status status);

    // 게임 정보 가져오기 (선택)
    Game findGameById(String gameId);
}
