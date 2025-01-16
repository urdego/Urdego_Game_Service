package io.urdego.urdego_game_service.domain.round.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter
@Setter
@RedisHash("question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    private String questionId;
    private double latitude;
    private double longitude;
    private String hint;
    private List<String> contents;

    @Builder
    public Question(String questionId, double latitude, double longitude, String hint, List<String> contents) {
        this.questionId = questionId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hint = hint;
        this.contents = contents;
    }
}
