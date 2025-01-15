package io.urdego.urdego_game_service.domain.round.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {

    @Id
    private String answerId;
    private String gameId;
    private int roundNum;
    private String userId;
    private double latitude;
    private double longitude;

    @Builder
    public Answer(String answerId, String gameId, int roundNum, String userId, double latitude, double longitude) {
        this.answerId = answerId;
        this.gameId = gameId;
        this.roundNum = roundNum;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
