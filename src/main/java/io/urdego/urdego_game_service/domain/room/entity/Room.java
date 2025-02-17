package io.urdego.urdego_game_service.domain.room.entity;

import io.urdego.urdego_game_service.common.enums.Status;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

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
    private Long hostId;
    private int maxPlayers;
    private int totalRounds;
    private List<Long> currentPlayers;
    private Map<Long, List<String>> playerContents;
    private Map<Long, Boolean> readyStatus;

    @Builder
    public Room(String roomId, String roomName, Status status, Long hostId, int maxPlayers, int totalRounds, List<Long> currentPlayers, Map<Long, List<String>> playerContents, Map<Long, Boolean> readyStatus) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.status = status;
        this.hostId = hostId;
        this.maxPlayers = maxPlayers;
        this.totalRounds = totalRounds;
        this.currentPlayers = currentPlayers;
        this.playerContents = playerContents;
        this.readyStatus = readyStatus;
    }

}
