package io.urdego.urdego_game_service.domain.round.service;

import io.urdego.urdego_game_service.domain.round.entity.Answer;
import io.urdego.urdego_game_service.domain.round.entity.Question;

public interface RoundService {
    // 문제 생성
    Question createQuestion(String gameId, int roundNum);

    // 정답 제출 및 거리 계산
    Answer submitAnswer(String gameId, int roundNum, String userId, double latitude, double longitude);
}
