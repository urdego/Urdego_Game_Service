package io.urdego.urdego_game_service.domain.game.service;

import io.urdego.urdego_game_service.controller.game.dto.request.GameCreateReq;
import io.urdego.urdego_game_service.controller.game.dto.response.GameCreateRes;
import io.urdego.urdego_game_service.controller.game.dto.response.GameEndRes;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;
import io.urdego.urdego_game_service.common.exception.game.GameException;
import io.urdego.urdego_game_service.domain.game.entity.Game;
import io.urdego.urdego_game_service.domain.game.repository.GameRepository;
import io.urdego.urdego_game_service.domain.room.entity.Room;
import io.urdego.urdego_game_service.domain.room.service.RoomService;
import io.urdego.urdego_game_service.domain.round.entity.Question;
import io.urdego.urdego_game_service.domain.round.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
                .scores(new HashMap<>())
                .startedAt(Instant.now())
                .build();

        // 초기 점수 세팅
        for (String player : room.getCurrentPlayers()) {
            game.getScores().put(player, 0);
        }

        // Question 생성
        for (int roundNum = 1; roundNum <= room.getTotalRounds(); roundNum++) {
            Question question = roundService.createQuestion(game.getRoomId());
            game.getQuestionIds().add(question.getQuestionId());
        }

        gameRepository.save(game);

        return GameCreateRes.from(game);
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
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(ExceptionMessage.GAME_NOT_FOUND));
    }

    // 게임 삭제 -> TTL로 처리?
    // 게임 정보 인메모리 말고 계속 저장?
}
