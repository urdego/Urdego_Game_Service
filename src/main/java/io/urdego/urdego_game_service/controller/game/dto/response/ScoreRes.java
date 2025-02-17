package io.urdego.urdego_game_service.controller.game.dto.response;


import io.urdego.urdego_game_service.domain.game.entity.Game;

import java.util.List;

public record ScoreRes(
        String roomId,
        int roundNum,
        boolean isLast,
        List<PlayerScore> roundScore,
        List<PlayerScore> totalScore
) {
    public static ScoreRes from(Game game, int roundNum, int totalRounds, List<PlayerScore> roundScore, List<PlayerScore> totalScore) {
        return new ScoreRes(
                game.getRoomId(),
                roundNum,
                roundNum == totalRounds,
                roundScore,
                totalScore
        );
    }
}
