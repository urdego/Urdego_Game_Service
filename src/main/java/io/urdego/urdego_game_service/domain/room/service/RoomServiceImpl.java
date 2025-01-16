package io.urdego.urdego_game_service.domain.room.service;

import io.urdego.urdego_game_service.api.room.dto.request.ContentSelectReq;
import io.urdego.urdego_game_service.api.room.dto.request.RoomCreateReq;
import io.urdego.urdego_game_service.api.room.dto.response.RoomCreateRes;
import io.urdego.urdego_game_service.api.room.dto.response.RoomInfoRes;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;
import io.urdego.urdego_game_service.common.exception.room.RoomException;
import io.urdego.urdego_game_service.domain.room.entity.Room;
import io.urdego.urdego_game_service.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    // 대기방 생성
    @Override
    public RoomCreateRes createRoom(RoomCreateReq request) {
        List<String> currentPlayers = new ArrayList<>();
        currentPlayers.add(request.userId());

        Room room = Room.builder()
                .roomId(UUID.randomUUID().toString())
                .status(Status.WAITING)
                .maxPlayers(request.maxPlayers())
                .totalRounds(request.totalRounds())
                .timer(request.timer())
                .currentPlayers(currentPlayers)
                .playerContents(new HashMap<>())
                .build();

        roomRepository.save(room);

        return RoomCreateRes.from(room);
    }

    // 대기방 참여
    @Override
    public RoomInfoRes joinRoom(String roomId, Long userId) {
        Room room = findRoomById(roomId);
        if (room.getCurrentPlayers().size() >= room.getMaxPlayers()) {
            throw new RoomException(ExceptionMessage.ROOM_FULL);
        }
        room.getCurrentPlayers().add(userId.toString());

        roomRepository.save(room);

        return RoomInfoRes.from(room);
    }

    // 컨텐츠 등록
    @Override
    public void registerContents(ContentSelectReq request) {
        Room room = findRoomById(request.roomId());
        if (request.contentIds().size() > 5) {
            throw new RoomException(ExceptionMessage.CONTENTS_OVER);
        }

        room.getPlayerContents().put(request.userId().toString(), request.contentIds());
        roomRepository.save(room);
    }

    // 컨텐츠 수정

    // 대기방 상태 변경
    @Override
    public Room updateRoomStatusById(String roomId, Status status) {
        Room room = findRoomById(roomId);
        room.setStatus(status);

        return roomRepository.save(room);
    }

    // 방 정보 조회
    @Override
    @Transactional(readOnly = true)
    public Room findRoomById(String roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomException(ExceptionMessage.ROOM_NOT_FOUND));
    }

    // 방 삭제 -> TTL로 처리?
}
