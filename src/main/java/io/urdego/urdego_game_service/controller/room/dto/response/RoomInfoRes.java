package io.urdego.urdego_game_service.controller.room.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.room.entity.Room;

public record RoomInfoRes(
        String roomId,
        Status status,
        String roomName,
        int maxPlayers,
        int currentPlayersCount,
        int totalRounds,
        PlayerRes hostInfo
) {
    public static RoomInfoRes from(Room room, PlayerRes hostInfo) {
        return new RoomInfoRes(
                room.getRoomId(),
                room.getStatus(),
                room.getRoomName(),
                room.getMaxPlayers(),
                room.getCurrentPlayers().size(),
                room.getTotalRounds(),
                hostInfo
        );
    }
}
