package io.urdego.urdego_game_service.domain.round.service;

import io.urdego.urdego_game_service.common.client.ContentServiceClient;
import io.urdego.urdego_game_service.common.client.dto.ContentRes;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;
import io.urdego.urdego_game_service.common.exception.round.QuestionException;
import io.urdego.urdego_game_service.domain.game.entity.Game;
import io.urdego.urdego_game_service.domain.game.service.GameService;
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

    @Override
    public Question createQuestion(String roomId, int roundNum) {
        Room room = roomService.findRoomById(roomId);

        List<String> allContents = room.getPlayerContents().values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        // 총 3개 이상의 컨텐츠가 준비되지 않았으면?
        if (allContents.size() < 3) {
            int needed = 3 - allContents.size();
            List<ContentRes> serviceContents = contentServiceClient.getUrdegoContents(needed);
            serviceContents.forEach(content -> allContents.add(content.contentId()));
        }

        Collections.shuffle(allContents);
        List<String> selectedContents = allContents.stream().limit(3).collect(Collectors.toList());

        // 첫 번째 컨텐츠 기준으로 정답 설정
        ContentRes firstContent = contentServiceClient.getContent(selectedContents.get(0));

        Question question = Question.builder()
                .questionId(UUID.randomUUID().toString())
                .latitude(firstContent.latitude())
                .longitude(firstContent.longitude())
                .hint(firstContent.hint())
                .contents(selectedContents)
                .build();

        return questionRepository.save(question);
    }

    // 유저별 정답 제출
    @Override
    public Answer submitAnswer(String gameId, int roundNum, String userId, double latitude, double longitude) {
        Question question = questionRepository.findById(gameId + ":" + roundNum)
                .orElseThrow(() -> new QuestionException(ExceptionMessage.QUESTION_NOT_FOUND));

        double distance = calculateDistance(latitude, longitude, question.getLatitude(), question.getLongitude());

        Answer answer = Answer.builder()
                .answerId(UUID.randomUUID().toString())
                .gameId(gameId)
                .roundNum(roundNum)
                .userId(userId)
                .latitude(latitude)
                .longitude(longitude)
                .build();

        return answerRepository.save(answer);
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
        return EARTH_RADIUS * c;
    }
}
