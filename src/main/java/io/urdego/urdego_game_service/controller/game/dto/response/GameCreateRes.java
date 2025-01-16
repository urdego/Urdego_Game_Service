package io.urdego.urdego_game_service.controller.game.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.game.entity.Game;

import java.util.List;
import java.util.Map;

public record GameCreateRes(
        String gameId,
        Status status,
        List<String> players,
        List<String> questionIds,
        Map<String, Integer> scores
) {
    public static GameCreateRes from(Game game) {
        return new GameCreateRes(
                game.getGameId(),
                game.getStatus(),
                game.getPlayers(),
                game.getQuestionIds(),
                game.getScores()
        );
    }
}
