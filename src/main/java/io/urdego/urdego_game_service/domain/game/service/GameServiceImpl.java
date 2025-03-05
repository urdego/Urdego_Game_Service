package io.urdego.urdego_game_service.domain.game.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.urdego.urdego_game_service.common.exception.player.PlayerException;
import io.urdego.urdego_game_service.controller.client.user.UserServiceClient;
import io.urdego.urdego_game_service.controller.game.dto.request.GameCreateReq;
import io.urdego.urdego_game_service.controller.game.dto.request.ScoreReq;
import io.urdego.urdego_game_service.controller.game.dto.response.*;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;
import io.urdego.urdego_game_service.common.exception.game.GameException;
import io.urdego.urdego_game_service.controller.room.dto.response.PlayerRes;
import io.urdego.urdego_game_service.domain.game.entity.Game;
import io.urdego.urdego_game_service.domain.game.repository.GameRepository;
import io.urdego.urdego_game_service.domain.player.entity.Player;
import io.urdego.urdego_game_service.domain.player.service.PlayerService;
import io.urdego.urdego_game_service.domain.room.entity.Room;
import io.urdego.urdego_game_service.domain.room.service.RoomService;
import io.urdego.urdego_game_service.domain.round.entity.Question;
import io.urdego.urdego_game_service.domain.round.service.RoundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final RoomService roomService;
    private final RoundService roundService;
    private final PlayerService playerService;
    private final UserServiceClient userServiceClient;
    private final RedissonClient redissonClient;

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
        List<Question> questions = roundService.createQuestions(game.getRoomId(), room.getTotalRounds());
        game.setQuestionIds(questions.stream().map(Question::getQuestionId).toList());

        gameRepository.save(game);
        log.info("게임 생성 | gameId: {}, roomId: {}", game.getGameId(), game.getRoomId());

        return GameCreateRes.from(game);
    }

    // 점수 조회
    @Override
    public ScoreRes giveScores(ScoreReq request) {
        Game game = findGameById(request.gameId());

        if (request.roundNum() > game.getQuestionIds().size()) {
            throw new GameException(ExceptionMessage.INVALID_ROUND, "roundNum: " + request.roundNum());
        }

        String roundKey = String.valueOf(request.roundNum());

        Map<Long, Integer> roundScores = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        if (game.getRoundScores().containsKey(roundKey)) {
            try {
                roundScores = objectMapper.readValue(game.getRoundScores().get(roundKey), new TypeReference<Map<Long, Integer>>() {});
            } catch (JsonProcessingException e) {
                log.error("JSON 역직렬화 실패 | roundScores: {}", game.getRoundScores().get(roundKey));
            }
        }
        Map<Long, Integer> totalScores = game.getTotalScores();

        Room room = roomService.findRoomById(game.getRoomId());
        List<Long> playerIds = room.getCurrentPlayers();
        List<Player> players = playerIds.stream()
                .map(playerService::getPlayer)
                .toList();

        List<PlayerScore> roundScoreList = calculateRanking(roundScores, players);
        List<PlayerScore> totalScoreList = calculateRanking(totalScores, players);

        log.info("{}라운드 점수 조회 완료 | {}", request.roundNum(), roundScores);

        return ScoreRes.from(game, request.roundNum(), room.getTotalRounds(), roundScoreList, totalScoreList);
    }

    // 게임 종료 (분산 락 적용)
    @Override
    public GameEndRes finishGame(String gameId) {
        String lockKey = "lock:game:" + gameId;
        RLock lock = redissonClient.getLock(lockKey);

        Game game = findGameById(gameId);

        if (game.getStatus() == Status.COMPLETED) {
            return GameEndRes.of(game, getExpList(game), getLevelList(game));
        }

        try {
            log.info("🔒 게임 종료 락 획득 시도 | gameId: {}", gameId);
            boolean available = lock.tryLock(5, 20, TimeUnit.SECONDS);
            if (!available) {
                throw new GameException(ExceptionMessage.GAME_ALREADY_COMPLETED, "게임 종료 중 다른 요청이 처리됨");
            }

            log.info("✅ 게임 종료 락 획득 성공 | gameId: {}", gameId);
            game = updateGameStatusById(gameId, Status.COMPLETED);

            game.setEndedAt(Instant.now());
            log.info("게임 종료 | gameId: {}, endedAt: {}", game.getGameId(), game.getEndedAt());

            List<GameEndRes.Exp> expList = calculateExp(game.getTotalScores());
            log.info("경험치 계산 결과 | {}", expList);

            List<LevelRes> levelList = userServiceClient.addUserExp(expList);

            return GameEndRes.of(game, expList, levelList);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();;
            throw new RuntimeException("🔓 게임 종료 락 획득 실패", e);

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("게임 종료 락 해제 | gameId: {}", gameId);
            }
        }
    }

    // 게임 정보 조회
    private Game findGameById(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(ExceptionMessage.GAME_NOT_FOUND));
    }

    // 해당 게임 플레이어들의 경험치 계산
    private List<GameEndRes.Exp> getExpList(Game game) {
        return calculateExp(game.getTotalScores());
    }

    // 해당 게임 플레이어들의 레벨 반영
    private List<LevelRes> getLevelList(Game game) {
        return userServiceClient.addUserExp(getExpList(game));
    }

    // 게임 상태 변경
    private Game updateGameStatusById(String gameId, Status status) {
        Game game = findGameById(gameId);
        game.setStatus(status);

        Game updatedGame = gameRepository.save(game);
        log.info("게임 상태 변경 | gameId: {}, Status: {}", game.getGameId(), game.getStatus());

        return updatedGame;
    }

    // 경험치 계산 (점수의 0.1%)
    private List<GameEndRes.Exp> calculateExp(Map<Long, Integer> totalScores) {
        return totalScores.entrySet().stream()
                .map(entry -> new GameEndRes.Exp(
                        entry.getKey(),
                        (long) Math.ceil(entry.getValue() * 0.01)
                ))
                .toList();
    }

    // 랭킹 계산
    private List<PlayerScore> calculateRanking(Map<Long, Integer> scoreMap, List<Player> players) {
        Map<Long, Player> playerMap = players.stream()
                .collect(Collectors.toMap(Player::getUserId, player -> player));

        AtomicInteger rank = new AtomicInteger(1);

        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(entry -> {
                    Long userId = entry.getKey();
                    int score = entry.getValue();

                    Player playerInfo = playerMap.get(userId);
                    if (playerInfo == null) {
                        throw new PlayerException(ExceptionMessage.USER_NOT_FOUND, "userId: " + userId);
                    }

                    return PlayerScore.from(rank.getAndIncrement(), PlayerRes.from(playerInfo), score);
                })
                .toList();
    }
}
