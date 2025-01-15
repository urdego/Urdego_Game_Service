package io.urdego.urdego_game_service.domain.round.repository;

import io.urdego.urdego_game_service.domain.round.entity.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends CrudRepository<Question, String> {
}
