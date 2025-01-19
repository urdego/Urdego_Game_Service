package io.urdego.urdego_game_service.domain.round.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "answer", timeToLive = 3600)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {

    @Id
    private String answerId;
    private String questionId;
    private String userId;
    private double latitude;
    private double longitude;
    private int score;

    @Builder
    public Answer(String answerId, String questionId, String userId, double latitude, double longitude, int score) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.score = score;
    }
}
