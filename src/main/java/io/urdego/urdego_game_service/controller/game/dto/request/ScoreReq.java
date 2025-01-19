package io.urdego.urdego_game_service.controller.game.dto.request;

public record ScoreReq(
        String gameId,
        int roundNum
) {
}
