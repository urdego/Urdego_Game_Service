package io.urdego.urdego_game_service.domain.round.service;

import io.urdego.urdego_game_service.controller.round.dto.request.AnswerReq;
import io.urdego.urdego_game_service.controller.round.dto.request.CoordinateReq;
import io.urdego.urdego_game_service.controller.round.dto.request.QuestionReq;
import io.urdego.urdego_game_service.controller.round.dto.response.AnswerRes;
import io.urdego.urdego_game_service.controller.round.dto.response.CoordinateRes;
import io.urdego.urdego_game_service.controller.round.dto.response.QuestionRes;
import io.urdego.urdego_game_service.controller.client.content.ContentServiceClient;
import io.urdego.urdego_game_service.controller.client.content.dto.ContentRes;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;
import io.urdego.urdego_game_service.common.exception.round.QuestionException;
import io.urdego.urdego_game_service.domain.player.entity.Player;
import io.urdego.urdego_game_service.domain.player.service.PlayerService;
import io.urdego.urdego_game_service.domain.room.entity.Room;
import io.urdego.urdego_game_service.domain.room.service.RoomService;
import io.urdego.urdego_game_service.domain.round.entity.Answer;
import io.urdego.urdego_game_service.domain.round.entity.Question;
import io.urdego.urdego_game_service.domain.round.repository.AnswerRepository;
import io.urdego.urdego_game_service.domain.round.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoundServiceImpl implements RoundService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ContentServiceClient contentServiceClient;
    private final RoomService roomService;
    private final PlayerService playerService;

    // 문제 생성
    @Override
    public Question createQuestion(String roomId, int roundNum) {
        Room room = roomService.findRoomById(roomId);

        // 해당 게임의 기존 문제 조회
        List<Question> existingQuestions = questionRepository.findAllByRoomId(roomId);

        List<String> allContents = room.getPlayerContents().values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        // 총 3개 이상의 컨텐츠가 준비되지 않았으면?
        if (allContents.size() < 3) {
            int needed = 3 - allContents.size();
            log.info("사용자 제공 컨텐츠 부족 | 필요 추가 컨텐츠: {}개", needed);
            List<ContentRes> serviceContents = contentServiceClient.getUrdegoContents(needed);
            serviceContents.forEach(content -> allContents.add(content.contentId().toString()));
        }

        Collections.shuffle(allContents);
        Question newQuestion;

        do {
            // 첫 번째 컨텐츠 기준으로 정답 설정
            ContentRes firstContent = contentServiceClient.getContent(Long.valueOf(allContents.get(0)));
            double targetLatitude = firstContent.latitude();
            double targetLongitude = firstContent.longitude();

            // 동일한 위치를 가진 컨텐츠들로 필터링
            List<String> selectedContents = allContents.stream()
                    .filter(contentId -> {
                        ContentRes content = contentServiceClient.getContent(Long.valueOf(contentId));
                        return content.latitude() == targetLatitude && content.longitude() == targetLongitude;
                    })
                    .limit(3)
                    .collect(Collectors.toList());

            newQuestion = Question.builder()
                    .roomId(roomId)
                    .roundNum(roundNum)
                    .latitude(targetLatitude)
                    .longitude(targetLongitude)
                    .name(firstContent.contentName())
                    .address(firstContent.address())
                    .hint(firstContent.hint())
                    .contents(selectedContents)
                    .build();
        } while (isDuplicateQuestion(newQuestion, existingQuestions));

        log.info("문제 생성 | roomId: {}, roundNum: {}", roomId, roundNum);

        return questionRepository.save(newQuestion);
    }

    // 문제 출제
    @Override
    public QuestionRes getQuestion(QuestionReq request) {
        Question question = findQuestionByRoomIdAndRoundNum(request.roomId(), request.roundNum());
        log.info("문제 출제 | roomId: {}, roundNum: {}", request.roomId(), request.roundNum());

        return QuestionRes.from(question);
    }

    // 유저별 정답 제출 및 거리, 점수 계산
    @Override
    public AnswerRes submitAnswer(AnswerReq request) {
        Question question = findQuestionById(request.questionId());

        double distance = calculateDistance(request.latitude(), request.longitude(), question.getLatitude(), question.getLongitude());
        int score = calculateScore(distance);

        Answer answer = Answer.builder()
                .answerId(UUID.randomUUID().toString())
                .userId(request.userId())
                .questionId(request.questionId())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .score(score)
                .build();

        answerRepository.save(answer);
        log.info("정답 저장 완료 | userId: {}, questionId: {}", answer.getUserId(), answer.getQuestionId());

        return AnswerRes.from(question.getRoomId(), answer);
    }

    // 라운드 결과 좌표 반환
    @Override
    public CoordinateRes roundResult(CoordinateReq request) {
        Question question = findQuestionById(request.questionId());
        List<Answer> answers = findAnswersByQuestionId(request.questionId());

        CoordinateRes.Coordinate answerCoordinate = new CoordinateRes.Coordinate(
                question.getLatitude(),
                question.getLongitude()
        );

        List<Long> userIds = answers.stream()
                .map(Answer::getUserId)
                .distinct()
                .toList();

        List<Player> players = userIds.stream()
                .map(playerService::getPlayer)
                .toList();

        Map<Long, Player> playerMap = players.stream()
                .collect(Collectors.toMap(Player::getUserId, player -> player));

        List<CoordinateRes.SubmitCoordinate> submitCoordinates = answers.stream()
                .map(answer -> {
                    Player player = playerMap.get(answer.getUserId());
                    return new CoordinateRes.SubmitCoordinate(
                            player.getNickname(),
                            player.getActiveCharacter(),
                            answer.getLatitude(),
                            answer.getLongitude()
                    );
                })
                .toList();

        return CoordinateRes.from(question, answerCoordinate, submitCoordinates);
    }

    // questionId로 정답 정보 조회
    @Override
    @Transactional(readOnly = true)
    public List<Answer> findAnswersByQuestionId(String questionId) {
        return answerRepository.findAllByQuestionId(questionId);
    }

    // roomId로 문제 정보 조회
    private Question findQuestionByRoomIdAndRoundNum(String roomId, int roundNum) {
        String questionId = roomId + ":" + roundNum;
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionException(ExceptionMessage.QUESTION_NOT_FOUND));
    }

    // questionId로 문제 정보 조회
    private Question findQuestionById(String questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionException(ExceptionMessage.QUESTION_NOT_FOUND));
    }

    // 중복 문제 체크 로직
    private boolean isDuplicateQuestion(Question newQuestion, List<Question> existingQuestions) {
        return existingQuestions.stream().anyMatch(existing ->
                existing.getLatitude() == newQuestion.getLatitude() &&
                existing.getLongitude() == newQuestion.getLongitude());
    }

    // 거리 계산
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // 지구 반경 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        log.info("거리 계산 | distance: {}", distance);
        return distance;
    }

    // 점수 계산
    private int calculateScore(double distance) {
        final int maxScore = 1000;
        final double maxDistance = 200.0;

        if (distance > maxDistance) {
            return 0;
        }

        int score = (int) Math.max(0, maxScore - (distance / maxDistance) * maxScore);

        log.info("점수 계산 | score: {}", score);
        return score;
    }
}
