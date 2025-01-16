package io.urdego.urdego_game_service.api.room.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.room.entity.Room;

public record RoomCreateRes(
        String roomId,
        Status status
) {
    public static RoomCreateRes from(Room room) {
        return new RoomCreateRes(
                room.getRoomId(),
                room.getStatus()
        );
    }
}
