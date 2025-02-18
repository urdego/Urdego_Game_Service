package io.urdego.urdego_game_service.domain.player.service;

import io.urdego.urdego_game_service.common.exception.ExceptionMessage;
import io.urdego.urdego_game_service.common.exception.player.PlayerException;
import io.urdego.urdego_game_service.controller.client.user.UserServiceClient;
import io.urdego.urdego_game_service.controller.client.user.dto.UserRes;
import io.urdego.urdego_game_service.domain.player.entity.Player;
import io.urdego.urdego_game_service.domain.player.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public Player getPlayer(Long userId) {
        return playerRepository.findById(userId)
                .orElseGet(() -> fetchAndCachePlayer(userId));
    }

    @Override
    public void deletePlayers(Set<Long> userIds) {
        userIds.forEach(playerRepository::deleteById);
    }

    private Player fetchAndCachePlayer(Long userId) {
        UserRes userRes = userServiceClient.getSimpleUser(userId);
        if (userRes == null) {
            throw new PlayerException(ExceptionMessage.USER_NOT_FOUND);
        }

        Player player = Player.from(userRes);
        playerRepository.save(player);

        return player;
    }

}
