package io.urdego.urdego_game_service.controller.game.dto.response;

public record LevelRes(
        Long userId,
        int level,
        Long totalExp,
        boolean isLevelUp
) {
}
