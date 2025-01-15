package io.urdego.urdego_game_service.domain.room.service;

import io.urdego.urdego_game_service.api.room.dto.request.RoomCreateReq;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;
import io.urdego.urdego_game_service.common.exception.room.RoomException;
import io.urdego.urdego_game_service.domain.room.entity.Room;
import io.urdego.urdego_game_service.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Room createRoom(RoomCreateReq request) {
        Room room = Room.builder()
                .roomId(UUID.randomUUID().toString())
                .status(Status.WAITING)
                .maxPlayers(request.maxPlayers())
                .totalRounds(request.totalRounds())
                .timer(request.timer())
                .currentPlayers(request.currentPlayers())
                .playerContents(new HashMap<>())
                .build();

        return roomRepository.save(room);
    }

    // 대기방 참여
    @Override
    public Room joinRoom(String roomId, Long userId) {
        Room room = findRoomById(roomId);
        if (room.getCurrentPlayers().size() >= room.getMaxPlayers()) {
            throw new RoomException(ExceptionMessage.ROOM_FULL);
        }
        room.getCurrentPlayers().add(userId.toString());

        return roomRepository.save(room);
    }

    // 컨텐츠 등록
    @Override
    public void registerContents(String roomId, Long userId, List<String> contentIds) {
        Room room = findRoomById(roomId);
        if (contentIds.size() > 5) {
            throw new RoomException(ExceptionMessage.CONTENTS_OVER);
        }

        room.getPlayerContents().put(userId.toString(), contentIds);
        roomRepository.save(room);
    }

    // 대기방 상태 변경
    @Override
    public Room updateRoomStatusById(String roomId, Status status) {
        Room room = findRoomById(roomId);
        room.setStatus(status);

        return roomRepository.save(room);
    }

    @Override
    public Room findRoomById(String roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomException(ExceptionMessage.ROOM_NOT_FOUND));
    }

    // 방 삭제 -> TTL로 처리?
}
