package io.urdego.urdego_game_service.api.room.dto.request;

public record PlayerInviteReq(
        String roomId, Long userId
) {
}
