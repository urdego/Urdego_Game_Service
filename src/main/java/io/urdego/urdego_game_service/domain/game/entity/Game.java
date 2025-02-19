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
@RedisHash(value = "game", timeToLive = 36000)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {

    @Id
    private String gameId;
    private String roomId;
    private Status status;
    private List<Long> players;
    private List<String> questionIds;
    private Map<Integer, Map<Long, Integer>> roundScores;
    private Map<Long, Integer> totalScores;
    private Instant startedAt;
    private Instant endedAt;

    @Builder
    public Game(String gameId, String roomId, Status status, List<Long> players, List<String> questionIds, Map<Integer, Map<Long, Integer>> roundScores, Map<Long, Integer> totalScores, Instant startedAt, Instant endedAt) {
        this.gameId = gameId;
        this.roomId = roomId;
        this.status = status;
        this.players = players;
        this.questionIds = questionIds;
        this.roundScores = roundScores;
        this.totalScores = totalScores;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

}
