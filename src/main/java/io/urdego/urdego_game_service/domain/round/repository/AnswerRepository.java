package io.urdego.urdego_game_service.domain.round.repository;

import io.urdego.urdego_game_service.domain.round.entity.Answer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends CrudRepository<Answer, String> {
    List<Answer> findAllByQuestionId(String questionId);
}
