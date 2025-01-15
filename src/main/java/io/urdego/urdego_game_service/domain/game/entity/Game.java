package io.urdego.urdego_game_service.domain.game.entity;

import io.urdego.urdego_game_service.common.enums.Status;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@RedisHash("game")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {

    @Id
    private String gameId;
    private String roomId;
    private Status status;
    private List<String> players;
    private List<String> questions;
    private Map<String, Integer> scores;
    private Instant startedAt;
    private Instant endedAt;

    @Builder
    public Game(String gameId, String roomId, Status status, List<String> players, List<String> questions, Map<String, Integer> scores, Instant startedAt, Instant endedAt) {
        this.gameId = gameId;
        this.roomId = roomId;
        this.status = status;
        this.players = players;
        this.questions = questions;
        this.scores = scores;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

}
