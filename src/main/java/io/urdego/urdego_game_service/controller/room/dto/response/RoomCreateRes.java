package io.urdego.urdego_game_service.controller.room.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.controller.client.user.dto.UserRes;
import io.urdego.urdego_game_service.domain.player.entity.Player;
import io.urdego.urdego_game_service.domain.room.entity.Room;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record RoomCreateRes(
        String roomId,
        String roomName,
        Status status
) {
    public static RoomCreateRes from(Room room) {

        return new RoomCreateRes(
                room.getRoomId(),
                room.getRoomName(),
                room.getStatus()
        );
    }
}
