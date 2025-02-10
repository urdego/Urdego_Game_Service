package io.urdego.urdego_game_service.controller.room.dto.response;

import io.urdego.urdego_game_service.controller.client.user.dto.UserRes;

public record PlayerRes(
        Long userId,
        String nickname,
        String activeCharacter
) {
    public static PlayerRes from(UserRes userRes) {
        return new PlayerRes(
                userRes.userId(),
                userRes.nickname(),
                userRes.activeCharacter()
        );
    }
}
