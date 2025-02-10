package io.urdego.urdego_game_service.controller.client.user.dto;

import java.util.List;

public record UserRes(
        Long userId,
        String nickname,
        String activeCharacter,
        List<String> ownedCharacters,
        int level,
        Long exp
) {
}
