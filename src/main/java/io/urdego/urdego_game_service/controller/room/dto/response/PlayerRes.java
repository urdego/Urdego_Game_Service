package io.urdego.urdego_game_service.controller.room.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.room.entity.Room;

import java.util.List;

public record PlayerRes(
        String roomId,
        Status status,
        List<String> currentPlayers
) {
    public static PlayerRes from(Room room) {
        return new PlayerRes(
                room.getRoomId(),
                room.getStatus(),
                room.getCurrentPlayers()
        );
    }
}
