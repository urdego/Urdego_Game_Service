package io.urdego.urdego_game_service.controller.round.dto.request;

public record CoordinateReq(
        String roomId,
        int roundNum,
        String questionId
) {
}
