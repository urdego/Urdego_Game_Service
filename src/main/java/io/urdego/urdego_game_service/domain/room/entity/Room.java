package io.urdego.urdego_game_service.domain.room.entity;

import io.urdego.urdego_game_service.common.enums.Status;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@RedisHash(value = "room", timeToLive = 36000)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

    @Id
    private String roomId;
    private String roomName;
    private Status status;
    private int maxPlayers;
    private int totalRounds;
    private List<String> currentPlayers;
    private Map<String, List<String>> playerContents;
    private Map<String, Boolean> readyStatus = new HashMap<>();

    @Builder
    public Room(String roomId, String roomName, Status status, int maxPlayers, int totalRounds, List<String> currentPlayers, Map<String, List<String>> playerContents, Map<String, Boolean> readyStatus) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.totalRounds = totalRounds;
        this.currentPlayers = currentPlayers;
        this.playerContents = playerContents;
        this.readyStatus = readyStatus;
    }

}
