package io.urdego.urdego_game_service.controller.game.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.game.entity.Game;

import java.util.List;
import java.util.Map;

public record GameEndRes(
        String gameId,
        String roomId,
        Status status,
        Map<Long, Integer> totalScores,
        List<Exp> expList,
        List<LevelRes> levelList
) {
    public static GameEndRes of(Game game, List<Exp> expList, List<LevelRes> levelList) {
        return new GameEndRes(
                game.getGameId(),
                game.getRoomId(),
                game.getStatus(),
                game.getTotalScores(),
                expList,
                levelList
        );
    }

    public record Exp(
            Long userId,
            Long exp
    ) {}
}
