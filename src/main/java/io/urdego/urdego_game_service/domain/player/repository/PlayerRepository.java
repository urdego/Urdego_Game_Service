package io.urdego.urdego_game_service.domain.player.repository;

import io.urdego.urdego_game_service.domain.player.entity.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
}
