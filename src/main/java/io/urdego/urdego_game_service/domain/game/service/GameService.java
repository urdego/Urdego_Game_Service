package io.urdego.urdego_game_service.domain.game.service;

import io.urdego.urdego_game_service.controller.game.dto.request.GameCreateReq;
import io.urdego.urdego_game_service.controller.game.dto.response.GameCreateRes;
import io.urdego.urdego_game_service.controller.game.dto.response.GameEndRes;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.game.entity.Game;
import org.springframework.transaction.annotation.Transactional;

public interface GameService {
    // 게임 생성
    GameCreateRes createGame(GameCreateReq request);

    // 게임 종료
    GameEndRes finishGame(String gameId);

    // 게임 상태 변경
    Game updateGameStatusById(String gameId, Status status);

    // 게임 정보 조회
    @Transactional(readOnly = true)
    Game findGameById(String gameId);
}
