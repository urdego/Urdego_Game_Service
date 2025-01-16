package io.urdego.urdego_game_service.controller.round.dto.request;

public record AnswerReq(
        String questionId,
        String userId,
        double latitude,
        double longitude
) {
}
