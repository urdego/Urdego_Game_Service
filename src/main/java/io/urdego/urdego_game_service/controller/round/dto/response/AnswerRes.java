package io.urdego.urdego_game_service.controller.round.dto.response;

import io.urdego.urdego_game_service.domain.round.entity.Answer;

public record AnswerRes(
        String questionId,
        String userId,
        double latitude,
        double longitude
) {
    public static AnswerRes from(Answer answer) {
        return new AnswerRes(
                answer.getQuestionId(),
                answer.getUserId(),
                answer.getLatitude(),
                answer.getLongitude()
        );
    }
}
