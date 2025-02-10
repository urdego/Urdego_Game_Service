package io.urdego.urdego_game_service.controller.game.dto.response;

import io.urdego.urdego_game_service.controller.room.dto.response.PlayerRes;

public record PlayerScore(
        int rank,
        Long userId,
        String nickname,
        String characterType,
        int score
) {
    public static PlayerScore from(int rank, PlayerRes playerRes, int score) {
        return new PlayerScore(
                rank,
                playerRes.userId(),
                playerRes.nickname(),
                playerRes.activeCharacter(),
                score
        );
    }
}
