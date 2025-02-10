package io.urdego.urdego_game_service.controller.room.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.room.entity.Room;

import java.util.List;

public record RoomCreateRes(
        String roomId,
        String roomName,
        Status status,
        List<String> currentPlayers
) {
    public static RoomCreateRes from(Room room) {
        return new RoomCreateRes(
                room.getRoomId(),
                room.getRoomName(),
                room.getStatus(),
                room.getCurrentPlayers()
        );
    }
}
