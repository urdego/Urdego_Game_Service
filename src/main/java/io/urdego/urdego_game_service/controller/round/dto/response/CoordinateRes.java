package io.urdego.urdego_game_service.controller.round.dto.response;

import io.urdego.urdego_game_service.domain.round.entity.Question;

import java.util.List;

public record CoordinateRes(
        String roomId,
        int roundNum,
        String placeName,
        String placeAddress,
        Coordinate answerCoordinate,
        List<SubmitCoordinate> submitCoordinates
) {
    public static CoordinateRes from(Question question, Coordinate answerCoordinate, List<SubmitCoordinate> submitCoordinates) {
        return new CoordinateRes(
                question.getRoomId(),
                question.getRoundNum(),
                question.getName(),
                question.getAddress(),
                answerCoordinate,
                submitCoordinates
        );
    }

    public record Coordinate(
            double lat,
            double lng
    ) {}

    public record SubmitCoordinate(
            String nickname,
            String characterType,
            double lat,
            double lng
    ) {}
}
