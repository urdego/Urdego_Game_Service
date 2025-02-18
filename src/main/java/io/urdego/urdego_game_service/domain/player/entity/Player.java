package io.urdego.urdego_game_service.domain.player.entity;

import io.urdego.urdego_game_service.controller.client.user.dto.UserRes;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter
@Setter
@RedisHash(value = "player", timeToLive = 36000)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {
    @Id
    private Long userId;
    private String nickname;
    private String activeCharacter;
    private List<String> ownedCharacters;
    private int level;
    private Long exp;

    @Builder
    public Player(Long userId, String nickname, String activeCharacter, List<String> ownedCharacters, int level, Long exp) {
        this.userId = userId;
        this.nickname = nickname;
        this.activeCharacter = activeCharacter;
        this.ownedCharacters = ownedCharacters;
        this.level = level;
        this.exp = exp;
    }

    public static Player from(UserRes userRes) {
        return Player.builder()
                .userId(userRes.userId())
                .nickname(userRes.nickname())
                .activeCharacter(userRes.activeCharacter())
                .ownedCharacters(userRes.ownedCharacters())
                .level(userRes.level())
                .exp(userRes.exp())
                .build();
    }
}
