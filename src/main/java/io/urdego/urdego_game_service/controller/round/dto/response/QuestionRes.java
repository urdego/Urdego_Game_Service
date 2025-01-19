package io.urdego.urdego_game_service.controller.round.dto.response;

import io.urdego.urdego_game_service.domain.round.entity.Question;

import java.util.List;

public record QuestionRes(
        String questionId,
        String roomId,
        int roundNum,
        double latitude,
        double longitude,
        String hint,
        List<String> contents
) {
    public static QuestionRes from(Question question) {
        return new QuestionRes(
                question.getQuestionId(),
                question.getRoomId(),
                question.getRoundNum(),
                question.getLatitude(),
                question.getLongitude(),
                question.getHint(),
                question.getContents()
        );
    }
}
