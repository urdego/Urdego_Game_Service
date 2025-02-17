package io.urdego.urdego_game_service.controller.game.dto.response;

import io.urdego.urdego_game_service.controller.client.user.dto.UserRes;
import io.urdego.urdego_game_service.controller.room.dto.response.PlayerRes;
import io.urdego.urdego_game_service.domain.game.entity.Game;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public record ScoreRes(
        String roomId,
        int roundNum,
        boolean isLast,
        List<PlayerScore> roundScore,
        List<PlayerScore> totalScore
) {
    public static ScoreRes from(Game game, int roundNum, int totalRounds, List<UserRes> users) {
        Map<Long, Integer> currentRoundScores = game.getRoundScores().getOrDefault(roundNum, new HashMap<>());
        Map<Long, Integer> currentTotalScores = game.getTotalScores();
        List<PlayerScore> roundScoreList = toRankingList(currentRoundScores, users);
        List<PlayerScore> totalScoreList = toRankingList(currentTotalScores, users);

        return new ScoreRes(
                game.getRoomId(),
                roundNum,
                roundNum == totalRounds,
                roundScoreList,
                totalScoreList
        );
    }

    private static List<PlayerScore> toRankingList(Map<Long, Integer> scoreMap, List<UserRes> users) {
        Map<Long, UserRes> userMap = users.stream()
                .collect(Collectors.toMap(UserRes::userId, userRes -> userRes));

        AtomicInteger rank = new AtomicInteger(1);
        return scoreMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry<Long, Integer>::getValue).reversed())
                .map(entry -> {
                    Long userId = entry.getKey();
                    int score = entry.getValue();

                    PlayerRes playerRes = Optional.ofNullable(userMap.get(userId))
                            .map(PlayerRes::from)
                            .orElse(PlayerRes.defaultInstance());

                    return PlayerScore.from(rank.getAndIncrement(), playerRes, score);
                })
                .toList();
    }
}
