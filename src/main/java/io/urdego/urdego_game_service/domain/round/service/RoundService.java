package io.urdego.urdego_game_service.domain.round.service;

import io.urdego.urdego_game_service.api.round.dto.request.AnswerReq;
import io.urdego.urdego_game_service.api.round.dto.request.QuestionReq;
import io.urdego.urdego_game_service.api.round.dto.response.AnswerRes;
import io.urdego.urdego_game_service.api.round.dto.response.QuestionRes;
import io.urdego.urdego_game_service.domain.round.entity.Question;
import org.springframework.transaction.annotation.Transactional;

public interface RoundService {
    // 문제 생성
    Question createQuestion(String roomId);

    // 문제 출제
    QuestionRes getQuestion(QuestionReq request);

    // 유저별 정답 제출 및 거리 계산
    AnswerRes submitAnswer(AnswerReq request);

    // 문제 가져오기
    @Transactional(readOnly = true)
    Question findQuestionById(String questionId);

}
