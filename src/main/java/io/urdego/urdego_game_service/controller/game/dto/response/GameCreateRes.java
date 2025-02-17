package io.urdego.urdego_game_service.controller.game.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.game.entity.Game;

import java.util.List;

public record GameCreateRes(
        String gameId,
        String roomId,
        Status status,
        List<Long> players,
        List<String> questionIds
) {
    public static GameCreateRes from(Game game) {
        return new GameCreateRes(
                game.getGameId(),
                game.getRoomId(),
                game.getStatus(),
                game.getPlayers(),
                game.getQuestionIds()
        );
    }
}
