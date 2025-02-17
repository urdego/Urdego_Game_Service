package io.urdego.urdego_game_service.domain.round.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "answer", timeToLive = 36000)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {

    @Id
    private String answerId;

    @Indexed
    private String questionId;

    private Long userId;
    private double latitude;
    private double longitude;
    private int score;

    @Builder
    public Answer(String answerId, String questionId, Long userId, double latitude, double longitude, int score) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.score = score;
    }
}
