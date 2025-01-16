package io.urdego.urdego_game_service.api.round;

import io.urdego.urdego_game_service.api.round.dto.request.AnswerReq;
import io.urdego.urdego_game_service.api.round.dto.request.QuestionReq;
import io.urdego.urdego_game_service.api.round.dto.response.AnswerRes;
import io.urdego.urdego_game_service.api.round.dto.response.QuestionRes;
import io.urdego.urdego_game_service.domain.round.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoundController {

    private final RoundService roundService;
    private final SimpMessagingTemplate messagingTemplate;

    // 라운드 문제 출제
    @MessageMapping("/round/question")
    public void giveQuestion(QuestionReq request) {
        QuestionRes response = roundService.getQuestion(request);
        messagingTemplate.convertAndSend("game-service/sub" + response.questionId(), response);
    }

    // 플레이어 정답 제출
    @MessageMapping("/round/answer")
    public void submitAnswer(AnswerReq request) {
        AnswerRes response = roundService.submitAnswer(request);
    }

}
