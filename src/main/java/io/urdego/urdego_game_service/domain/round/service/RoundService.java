package io.urdego.urdego_game_service.domain.round.service;

import io.urdego.urdego_game_service.controller.round.dto.request.AnswerReq;
import io.urdego.urdego_game_service.controller.round.dto.request.QuestionReq;
import io.urdego.urdego_game_service.controller.round.dto.response.AnswerRes;
import io.urdego.urdego_game_service.controller.round.dto.response.QuestionRes;
import io.urdego.urdego_game_service.domain.round.entity.Answer;
import io.urdego.urdego_game_service.domain.round.entity.Question;

import java.util.List;

public interface RoundService {
    // 문제 생성
    Question createQuestion(String roomId);

    // 문제 출제
    QuestionRes getQuestion(QuestionReq request);

    // 유저별 정답 제출 및 거리 계산
    AnswerRes submitAnswer(AnswerReq request);

    // questionId로 정답 정보 조회
    List<Answer> findAnswersByQuestionId(String questionId);
}
