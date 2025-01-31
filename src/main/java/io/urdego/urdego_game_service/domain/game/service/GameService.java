package io.urdego.urdego_game_service.domain.game.service;

import io.urdego.urdego_game_service.controller.game.dto.request.GameCreateReq;
import io.urdego.urdego_game_service.controller.game.dto.request.ScoreReq;
import io.urdego.urdego_game_service.controller.game.dto.response.GameCreateRes;
import io.urdego.urdego_game_service.controller.game.dto.response.GameEndRes;
import io.urdego.urdego_game_service.controller.game.dto.response.ScoreRes;

public interface GameService {
    // 게임 생성
    GameCreateRes createGame(GameCreateReq request);

    // 점수 계산
    ScoreRes giveScores(ScoreReq request);

    // 게임 종료
    GameEndRes finishGame(String gameId);
}
