package io.urdego.urdego_game_service.controller.room.dto.request;

public record RoomCreateReq(
        String userId,
        String roomName,
        int maxPlayers,
        int totalRounds,
        int timer
) {
}
