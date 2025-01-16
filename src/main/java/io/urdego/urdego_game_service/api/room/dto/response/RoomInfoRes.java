package io.urdego.urdego_game_service.api.room.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.room.entity.Room;

import java.util.List;

public record RoomInfoRes(
        String roomId,
        Status status,
        List<String> currentPlayers
) {
    public static RoomInfoRes from(Room room) {
        return new RoomInfoRes(
                room.getRoomId(),
                room.getStatus(),
                room.getCurrentPlayers()
        );
    }
}
