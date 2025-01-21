package io.urdego.urdego_game_service.domain.round.repository;

import io.urdego.urdego_game_service.domain.round.entity.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends CrudRepository<Question, String> {
    Optional<Question> findByRoomIdAndRoundNum(String roomId, int roundNum);

    List<Question> findAllByRoomId(String roomId);
}
