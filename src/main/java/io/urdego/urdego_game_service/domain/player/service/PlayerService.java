package io.urdego.urdego_game_service.domain.player.service;

import io.urdego.urdego_game_service.domain.player.entity.Player;

import java.util.Set;

public interface PlayerService {
    // 플레이어 조회
    Player getPlayer(Long userId);

    // 플레이어 삭제
    void deletePlayers(Set<Long> userIds);
}
