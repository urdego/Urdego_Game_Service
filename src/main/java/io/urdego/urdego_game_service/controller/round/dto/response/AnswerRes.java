package io.urdego.urdego_game_service.controller.round.dto.response;

import io.urdego.urdego_game_service.domain.round.entity.Answer;

public record AnswerRes(
        String roomId,
        String questionId,
        String userId,
        double latitude,
        double longitude
) {
    public static AnswerRes from(String roomId, Answer answer) {
        return new AnswerRes(
                roomId,
                answer.getQuestionId(),
                answer.getUserId(),
                answer.getLatitude(),
                answer.getLongitude()
        );
    }
}
