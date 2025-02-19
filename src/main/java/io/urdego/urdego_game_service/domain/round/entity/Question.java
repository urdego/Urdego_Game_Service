package io.urdego.urdego_game_service.domain.round.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter
@Setter
@RedisHash(value = "question", timeToLive = 36000)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    private String questionId;
    private String roomId;
    private int roundNum;
    private double latitude;
    private double longitude;
    private String name;
    private String address;
    private String hint;
    private List<String> contents;

    @Builder
    public Question(String roomId, int roundNum, double latitude, double longitude, String name, String address, String hint, List<String> contents) {
        this.questionId = roomId + ":" + roundNum;
        this.roomId = roomId;
        this.roundNum = roundNum;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
        this.hint = hint;
        this.contents = contents;
    }
}
