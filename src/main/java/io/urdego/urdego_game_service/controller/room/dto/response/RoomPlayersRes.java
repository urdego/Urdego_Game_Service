package io.urdego.urdego_game_service.controller.room.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.controller.client.user.dto.UserRes;
import io.urdego.urdego_game_service.domain.player.entity.Player;
import io.urdego.urdego_game_service.domain.room.entity.Room;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record RoomPlayersRes(
        String roomId,
        String roomName,
        Status status,
        List<PlayerRes> currentPlayers,
        String host,
        Map<String, Boolean> readyStatus,
        Boolean allReady
) {
    public static RoomPlayersRes from(Room room, List<PlayerRes> currentPlayers, String host, Map<String,Boolean> readyStatus, boolean allReady) {
        return new RoomPlayersRes(
                room.getRoomId(),
                room.getRoomName(),
                room.getStatus(),
                currentPlayers,
                host,
                readyStatus,
                allReady
        );
    }
}
