package io.urdego.urdego_game_service.domain.room.entity;

import io.urdego.urdego_game_service.common.enums.Status;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter
@Setter
@RedisHash("room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

    @Id
    private String roomId;
    private Status status;
    private int maxPlayers;
    private int totalRounds;
    private int timer;
    private List<String> currentPlayers;
    // private String gameType;

    @Builder
    public Room(String roomId, Status status, int maxPlayers, int totalRounds, int timer, List<String> currentPlayers) {
        this.roomId = roomId;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.totalRounds = totalRounds;
        this.timer = timer;
        this.currentPlayers = currentPlayers;
    }

}
