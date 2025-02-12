package io.urdego.urdego_game_service.controller.room.dto.response;

import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.controller.client.user.dto.UserRes;
import io.urdego.urdego_game_service.domain.room.entity.Room;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record RoomPlayersRes(
        String roomId,
        Status status,
        List<PlayerRes> currentPlayers,
        String host,
        Map<String, Boolean> readyStatus,
        Boolean allReady
) {
    public static RoomPlayersRes from(Room room, List<UserRes> users) {
        Map<String, String> userIdToNickname = users.stream()
                .collect(Collectors.toMap(userRes -> String.valueOf(userRes.userId()), UserRes::nickname));

        Map<String, UserRes> userIdToUserRes = users.stream()
                .collect(Collectors.toMap(userRes -> String.valueOf(userRes.userId()), userRes -> userRes));

        List<PlayerRes> currentPlayers = room.getCurrentPlayers().stream()
                .map(userId -> Optional.ofNullable(userIdToUserRes.get(userId))
                        .map(PlayerRes::from)
                        .orElse(PlayerRes.defaultInstance()))
                .toList();

        String host = room.getCurrentPlayers().isEmpty() ? null : userIdToNickname.get(room.getCurrentPlayers().get(0));

        Map<String, Boolean> readyStatus = room.getReadyStatus().entrySet().stream()
                .collect(Collectors.toMap(entry -> userIdToNickname.get(entry.getKey()), Map.Entry::getValue));

        boolean allReady = room.getReadyStatus().size() == room.getCurrentPlayers().size()
                && room.getReadyStatus().values().stream().allMatch(ready -> ready);

        return new RoomPlayersRes(
                room.getRoomId(),
                room.getStatus(),
                currentPlayers,
                host,
                readyStatus,
                allReady
        );
    }
}
