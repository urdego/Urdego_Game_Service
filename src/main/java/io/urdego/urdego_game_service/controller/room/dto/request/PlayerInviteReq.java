package io.urdego.urdego_game_service.controller.room.dto.request;

public record PlayerInviteReq(
        String roomId, Long userId
) {
}
