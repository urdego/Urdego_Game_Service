package io.urdego.urdego_game_service.controller.room.dto.request;

public record PlayerReq(
        String roomId,
        Long userId
) {
}
