package io.urdego.urdego_game_service.domain.game.repository;

import io.urdego.urdego_game_service.domain.game.entity.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, String> {
}
