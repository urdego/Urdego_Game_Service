package io.urdego.urdego_game_service.controller.room.dto.response;

import io.urdego.urdego_game_service.domain.player.entity.Player;

public record PlayerRes(
        Long userId,
        String nickname,
        String activeCharacter,
        int level
) {
    public static PlayerRes from(Player player) {
        return new PlayerRes(
                player.getUserId(),
                player.getNickname(),
                player.getActiveCharacter(),
                player.getLevel()
        );
    }
}
