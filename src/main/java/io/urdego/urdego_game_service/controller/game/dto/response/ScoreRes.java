package io.urdego.urdego_game_service.controller.game.dto.response;

import io.urdego.urdego_game_service.domain.game.entity.Game;

import java.util.Map;

public record ScoreRes(
        String roomId,
        Map<Integer, Map<String, Integer>> roundScores,
        Map<String, Integer> totalScores
) {
    public static ScoreRes from(Game game) {
        return new ScoreRes(
                game.getRoomId(),
                game.getRoundScores(),
                game.getTotalScores()
        );
    }
}
