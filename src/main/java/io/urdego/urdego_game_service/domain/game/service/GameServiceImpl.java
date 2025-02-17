package io.urdego.urdego_game_service.domain.game.service;

import io.urdego.urdego_game_service.controller.client.user.UserServiceClient;
import io.urdego.urdego_game_service.controller.client.user.dto.UserInfoListReq;
import io.urdego.urdego_game_service.controller.client.user.dto.UserRes;
import io.urdego.urdego_game_service.controller.game.dto.request.GameCreateReq;
import io.urdego.urdego_game_service.controller.game.dto.request.ScoreReq;
import io.urdego.urdego_game_service.controller.game.dto.response.GameCreateRes;
import io.urdego.urdego_game_service.controller.game.dto.response.GameEndRes;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;
import io.urdego.urdego_game_service.common.exception.game.GameException;
import io.urdego.urdego_game_service.controller.game.dto.response.ScoreRes;
import io.urdego.urdego_game_service.domain.game.entity.Game;
import io.urdego.urdego_game_service.domain.game.repository.GameRepository;
import io.urdego.urdego_game_service.domain.room.entity.Room;
import io.urdego.urdego_game_service.domain.room.service.RoomService;
import io.urdego.urdego_game_service.domain.round.entity.Answer;
import io.urdego.urdego_game_service.domain.round.entity.Question;
import io.urdego.urdego_game_service.domain.round.service.RoundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final RoomService roomService;
    private final RoundService roundService;
    private final UserServiceClient userServiceClient;

    // 게임 생성
    @Override
    public GameCreateRes createGame(GameCreateReq request) {
        Room room = roomService.updateRoomStatusById(request.roomId(), Status.COMPLETED);

        Game game = Game.builder()
                .gameId(UUID.randomUUID().toString())
                .roomId(room.getRoomId())
                .status(Status.IN_PROGRESS)
                .players(room.getCurrentPlayers())
                .questionIds(new ArrayList<>())
                .roundScores(new HashMap<>())
                .totalScores(new HashMap<>())
                .startedAt(Instant.now())
                .build();

        // 초기 점수 세팅
        for (Long player : room.getCurrentPlayers()) {
            game.getTotalScores().put(player, 0);
        }

        // Question 생성
        for (int roundNum = 1; roundNum <= room.getTotalRounds(); roundNum++) {
            log.info("{}라운드 문제 생성 중...", roundNum);
            Question question = roundService.createQuestion(game.getRoomId(), roundNum);
            game.getQuestionIds().add(question.getQuestionId());
        }

        gameRepository.save(game);
        log.info("게임 생성 | gameId: {}, roomId: {}", game.getGameId(), game.getRoomId());

        return GameCreateRes.from(game);
    }

    // 점수 계산
    @Override
    public ScoreRes giveScores(ScoreReq request) {
        Game game = findGameById(request.gameId());

        String questionId = game.getQuestionIds().get(request.roundNum() - 1);
        List<Answer> answers = roundService.findAnswersByQuestionId(questionId);

        if (answers.isEmpty()) {
            log.warn("점수 계산 실패 | gameId: {}, roundNum: {} | 정답 데이터가 없습니다.", request.gameId(), request.roundNum());
        }

        updateRoundScores(game, request.roundNum(), answers);
        updateTotalScores(game);

        gameRepository.save(game);
        log.info("게임 점수 정보 | roundScores: {}, totalScores: {}", game.getRoundScores(), game.getTotalScores());

        Room room = roomService.findRoomById(game.getRoomId());
        List<Long> userIds = room.getCurrentPlayers().stream()
                .map(Long::valueOf)
                .toList();

        List<UserRes> users = userServiceClient.getUsers(new UserInfoListReq(userIds));

        return ScoreRes.from(game, request.roundNum(), room.getTotalRounds(), users);
    }

    // 게임 종료
    @Override
    public GameEndRes finishGame(String gameId) {
        Game game = findGameById(gameId);
        if (game.getStatus() == Status.COMPLETED) {
            throw new GameException(ExceptionMessage.GAME_ALREADY_COMPLETED);
        }
        game = updateGameStatusById(gameId, Status.COMPLETED);

        game.setEndedAt(Instant.now());
        log.info("게임 종료 | gameId: {}, endedAt: {}", game.getGameId(), game.getEndedAt());

        Map<Long, Integer> exp = calculateExp(game.getTotalScores());
        log.info("경험치 계산 결과: {}", exp);

        playerService.deletePlayers(game.getTotalScores().keySet());

        return GameEndRes.of(game, exp);
    }

    // 게임 정보 조회
    private Game findGameById(String gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(ExceptionMessage.GAME_NOT_FOUND));

        if (game.getRoundScores() == null) {
            game.setRoundScores(new HashMap<>());
        }
        if (game.getTotalScores() == null) {
            game.setTotalScores(new HashMap<>());
        }

        gameRepository.save(game);

        return game;
    }

    // 게임 상태 변경
    private Game updateGameStatusById(String gameId, Status status) {
        Game game = findGameById(gameId);
        game.setStatus(status);

        Game updatedGame = gameRepository.save(game);
        log.info("게임 상태 변경 | gameId: {}, Status: {}", game.getGameId(), game.getStatus());

        return updatedGame;
    }

    // 라운드 점수 업뎃
    private void updateRoundScores(Game game, int roundNum, List<Answer> answers) {
        Map<Integer, Map<Long, Integer>> roundScores = game.getRoundScores();

        if (roundScores == null) {
            roundScores = new HashMap<>();
        }

        roundScores.putIfAbsent(roundNum, new HashMap<>());

        Map<Long, Integer> roundScore = roundScores.get(roundNum);
        for (Answer answer : answers) {
            log.info("Answer: userId={}, score={}", answer.getUserId(), answer.getScore());
            roundScore.put(answer.getUserId(), answer.getScore());
        }

        for (Long player : game.getPlayers()) {
            roundScore.putIfAbsent(player, 0);
        }

        game.setRoundScores(roundScores);

        log.info("{}라운드 점수가 업데이트 되었습니다. : {}", roundNum, game.getRoundScores());
    }

    // 전체 점수 업뎃
    private void updateTotalScores(Game game) {
        Map<Long, Integer> totalScores = game.getTotalScores();

        game.getRoundScores().forEach((roundNum, roundScore) -> {
            roundScore.forEach((userId, score) -> {
                totalScores.put(userId, totalScores.getOrDefault(userId, 0) + score);
            });
        });

        game.setTotalScores(totalScores);

        log.info("전체 점수가 업데이트 되었습니다. : {}", game.getTotalScores());
    }

    // 경험치 계산 (점수의 0.1%)
    private Map<Long, Integer> calculateExp(Map<Long, Integer> totalScores) {
        Map<Long, Integer> expMap = new HashMap<>();
        totalScores.forEach((userId, score) -> {
            int exp = (int) Math.ceil(score * 0.01);
            expMap.put(userId, exp);
        });

        return expMap;
    }
}
