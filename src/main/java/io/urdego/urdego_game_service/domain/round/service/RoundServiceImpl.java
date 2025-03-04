package io.urdego.urdego_game_service.domain.round.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.urdego.urdego_game_service.common.exception.game.GameException;
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
import io.urdego.urdego_game_service.domain.game.entity.Game;
import io.urdego.urdego_game_service.domain.game.repository.GameRepository;
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
    private final GameRepository gameRepository;
    private final ContentServiceClient contentServiceClient;
    private final RoomService roomService;
    private final PlayerService playerService;


    // 문제 생성
    @Override
    public List<Question> createQuestions(String roomId, int totalRounds) {
        Room room = roomService.findRoomById(roomId);
        ObjectMapper objectMapper = new ObjectMapper();

        List<ContentRes> allContents = room.getPlayerContents().values()
                .stream()
                .flatMap(jsonContent -> {
                    try {
                        return objectMapper.readValue(jsonContent, new TypeReference<List<String>>() {}).stream()
                                .map(contentId -> contentServiceClient.getContent(Long.valueOf(contentId)));
                    } catch (Exception e) {
                        throw new RuntimeException("JSON 역직렬화 실패", e);
                    }
                })
                .toList();

        log.info("등록된 플레이어 컨텐츠 | {}개", allContents.size());

        Map<String, List<ContentRes>> groupedContents = allContents.stream()
                        .collect(Collectors.groupingBy(content -> content.latitude() + "," + content.longitude()));

        List<List<ContentRes>> contentGroups = new ArrayList<>(groupedContents.values());

        if (contentGroups.size() < totalRounds) {
            int needed = totalRounds - contentGroups.size();
            log.info("자체 컨텐츠 추가 | {}개", needed);
            List<ContentRes> serviceContents = contentServiceClient.getUrdegoContents(needed);

            Map<String, List<ContentRes>> newGroupedContents = serviceContents.stream()
                    .collect(Collectors.groupingBy(content -> content.latitude() + "," + content.longitude()));

            contentGroups.addAll(newGroupedContents.values());
        }

        List<Question> questions = new ArrayList<>();
        for (int round = 0; round < totalRounds; round++) {
            List<ContentRes> roundContents = contentGroups.get(round);
            Question question = buildQuestion(roomId, round + 1, roundContents);
            questions.add(question);
        }

        log.info("문제 생성 | roomId: {}, 생성된 문제 개수: {}", roomId, questions.size());
        questionRepository.saveAll(questions);

        return questions;
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
        log.info("정답 저장 | userId: {}, questionId: {}", answer.getUserId(), answer.getQuestionId());

        Game game = gameRepository.findByRoomId(question.getRoomId())
                .orElseThrow(() -> new GameException(ExceptionMessage.GAME_NOT_FOUND));

        String roundKey = String.valueOf(question.getRoundNum());
        ObjectMapper objectMapper = new ObjectMapper();

        if (game.getRoundScores() == null) {
            game.setRoundScores(new HashMap<>());
        }
        if (game.getTotalScores() == null) {
            game.setTotalScores(new HashMap<>());
        }

        Map<Long, Integer> roundScoreMap = new HashMap<>();
        if (game.getRoundScores().containsKey(roundKey)) {
            try {
                roundScoreMap = objectMapper.readValue(game.getRoundScores().get(roundKey), new TypeReference<Map<Long, Integer>>() {});
            } catch (JsonProcessingException e) {
                log.error("JSON 역직렬화 실패 | roundScores: {}", game.getRoundScores().get(roundKey));
            }
        }

        // ✅ 점수 저장
        roundScoreMap.put(answer.getUserId(), answer.getScore());

        // ✅ JSON 직렬화 후 저장
        try {
            game.getRoundScores().put(roundKey, objectMapper.writeValueAsString(roundScoreMap));
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 실패 | roundScoreMap: {}", roundScoreMap);
        }

        // ✅ 전체 점수 업데이트
        game.getTotalScores().merge(answer.getUserId(), score, Integer::sum);

        // ✅ 게임 저장
        gameRepository.save(game);

        log.info("점수 반영 | userId: {}, roundNum: {}, score: {}, gameId: {}", answer.getUserId(), question.getRoundNum(), score, game.getGameId());
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

    // 문제 저장
    private Question buildQuestion(String roomId, int roundNum, List<ContentRes> roundContents) {
        ContentRes firstContent = roundContents.get(0);
        double targetLatitude = firstContent.latitude();
        double targetLongitude = firstContent.longitude();

        List<String> selectedContents = roundContents.stream()
                .map(ContentRes::url)
                .limit(3)
                .collect(Collectors.toList());

        return Question.builder()
                .roomId(roomId)
                .roundNum(roundNum)
                .latitude(targetLatitude)
                .longitude(targetLongitude)
                .name(firstContent.contentName())
                .address(firstContent.address())
                .hint(firstContent.hint())
                .contents(selectedContents)
                .build();
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
