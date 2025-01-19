package io.urdego.urdego_game_service.controller.game.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.game.entity.Game;

import java.util.Map;

public record GameEndRes(
        String gameId,
        String roomId,
        Status status,
        Map<String, Integer> totalScores
) {
    public static GameEndRes from(Game game) {
        return new GameEndRes(
                game.getGameId(),
                game.getRoomId(),
                game.getStatus(),
                game.getTotalScores()
        );
    }
}
