package io.urdego.urdego_game_service.api.room.dto.request;

import java.util.List;

public record ContentSelectReq(
        String roomId,
        Long userId,
        List<String> contentIds
) {
}
