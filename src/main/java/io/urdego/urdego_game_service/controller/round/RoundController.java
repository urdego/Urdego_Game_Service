package io.urdego.urdego_game_service.controller.round;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.urdego.urdego_game_service.controller.round.dto.request.AnswerReq;
import io.urdego.urdego_game_service.controller.round.dto.request.QuestionReq;
import io.urdego.urdego_game_service.controller.round.dto.response.AnswerRes;
import io.urdego.urdego_game_service.controller.round.dto.response.QuestionRes;
import io.urdego.urdego_game_service.domain.round.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game-service/round")
public class RoundController {

    private final RoundService roundService;

    @Tag(name = "백엔드 API")
    @Operation(summary = "문제 출제", description = "라운드 시작")
    @PostMapping("/question")
    public ResponseEntity<QuestionRes> giveQuestion(@RequestBody QuestionReq request) {
        QuestionRes response = roundService.getQuestion(request);
        return ResponseEntity.ok(response);
    }

    @Tag(name = "백엔드 API")
    @Operation(summary = "답안 제출", description = "사용자별 답안 제출")
    @PostMapping("/answer")
    public ResponseEntity<AnswerRes> submitAnswer(@RequestBody AnswerReq request) {
        AnswerRes response = roundService.submitAnswer(request);
        return ResponseEntity.ok(response);
    }

}
