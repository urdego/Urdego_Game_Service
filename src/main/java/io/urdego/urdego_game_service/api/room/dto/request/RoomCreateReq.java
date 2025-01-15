package io.urdego.urdego_game_service.api.room.dto.request;

import java.util.List;

public record RoomCreateReq(
        int maxPlayers,
        int totalRounds,
        int timer,
        List<String> currentPlayers
) {
}
