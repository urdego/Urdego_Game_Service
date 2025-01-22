package io.urdego.urdego_game_service.domain.game.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final RoomService roomService;
    private final RoundService roundService;

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
        for (String player : room.getCurrentPlayers()) {
            game.getTotalScores().put(player, 0);
        }

        // Question 생성
        for (int roundNum = 1; roundNum <= room.getTotalRounds(); roundNum++) {
            Question question = roundService.createQuestion(game.getRoomId(), roundNum);
            game.getQuestionIds().add(question.getQuestionId());
        }

        gameRepository.save(game);

        return GameCreateRes.from(game);
    }

    // 점수 계산
    @Override
    public ScoreRes giveScores(ScoreReq request) {
        Game game = findGameById(request.gameId());

        String questionId = game.getQuestionIds().get(request.roundNum() - 1);
        List<Answer> answers = roundService.findAnswersByQuestionId(questionId);

        updateRoundScores(game, request.roundNum(), answers);
        updateTotalScores(game);

        gameRepository.save(game);

        return ScoreRes.from(game);
    }

    // 게임 종료
    @Override
    public GameEndRes finishGame(String gameId) {
        Game game = findGameById(gameId);

        if (game.getStatus() == Status.COMPLETED) {
            throw new GameException(ExceptionMessage.GAME_ALREADY_COMPLETED);
        }

        game.setStatus(Status.COMPLETED);
        game.setEndedAt(Instant.now());

        gameRepository.save(game);

        return GameEndRes.from(game);
    }

    // 게임 상태 변경
    @Override
    public Game updateGameStatusById(String gameId, Status status) {
        Game game = findGameById(gameId);
        game.setStatus(status);

        return gameRepository.save(game);
    }

    // 게임 정보 조회
    @Transactional(readOnly = true)
    @Override
    public Game findGameById(String gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(ExceptionMessage.GAME_NOT_FOUND));

        if (game.getRoundScores() == null) {
            game.setRoundScores(new HashMap<>());
        }
        if (game.getTotalScores() == null) {
            game.setTotalScores(new HashMap<>());
        }

        return game;
    }

    // 라운드 점수 업뎃
    private void updateRoundScores(Game game, int roundNum, List<Answer> answers) {
        Map<Integer, Map<String, Integer>> roundScores = game.getRoundScores();

        if (!roundScores.containsKey(roundNum)) {
            roundScores.put(roundNum, new HashMap<>());
        }

        Map<String, Integer> roundScore = roundScores.get(roundNum);
        for (Answer answer : answers) {
            roundScore.put(answer.getUserId(), answer.getScore());
        }

        game.setRoundScores(roundScores);
    }

    // 전체 점수 업뎃
    private void updateTotalScores(Game game) {
        Map<String, Integer> totalScores = game.getTotalScores();

        game.getRoundScores().forEach((roundNum, roundScore) -> {
            roundScore.forEach((userId, score) -> {
                totalScores.put(userId, totalScores.getOrDefault(userId, 0) + score);
            });
        });

        game.setTotalScores(totalScores);
    }
}
