package io.urdego.urdego_game_service.controller.room.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.room.entity.Room;

import java.util.List;
import java.util.Map;

public record RoomPlayersRes(
        String roomId,
        Status status,
        List<String> currentPlayers,
        String isHost,
        Map<String, Boolean> readyStatus,
        Boolean allReady
) {
    public static RoomPlayersRes from(Room room) {
        String hostId = room.getCurrentPlayers().isEmpty() ? null : room.getCurrentPlayers().get(0);
        boolean allReady = room.getReadyStatus().size() == room.getCurrentPlayers().size()
                && room.getReadyStatus().values().stream().allMatch(ready -> ready);

        return new RoomPlayersRes(
                room.getRoomId(),
                room.getStatus(),
                room.getCurrentPlayers(),
                hostId,
                room.getReadyStatus(),
                allReady
        );
    }
}
