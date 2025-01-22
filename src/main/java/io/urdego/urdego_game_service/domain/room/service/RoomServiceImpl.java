package io.urdego.urdego_game_service.domain.room.service;

import io.urdego.urdego_game_service.controller.room.dto.request.ContentSelectReq;
import io.urdego.urdego_game_service.controller.room.dto.request.PlayerReq;
import io.urdego.urdego_game_service.controller.room.dto.request.RoomCreateReq;
import io.urdego.urdego_game_service.controller.room.dto.response.PlayerRes;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomCreateRes;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomInfoRes;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;
import io.urdego.urdego_game_service.common.exception.room.RoomException;
import io.urdego.urdego_game_service.domain.room.entity.Room;
import io.urdego.urdego_game_service.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    // 대기방 생성
    @Override
    public RoomCreateRes createRoom(RoomCreateReq request) {
        List<String> currentPlayers = new ArrayList<>();
        currentPlayers.add(request.userId());

        String roomName = (request.roomName() == null || request.roomName().isBlank())
                ? "어데고 게임방" : request.roomName();

        Room room = Room.builder()
                .roomId(UUID.randomUUID().toString())
                .roomName(roomName)
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

    // 대기방 리스트 조회
    @Override
    public List<RoomInfoRes> getRoomList() {
        Iterable<Room> rooms = roomRepository.findAll();
        List<RoomInfoRes> roomList = new ArrayList<>();

        for (Room room : rooms) {
            if (room != null) {
                roomList.add(RoomInfoRes.from(room));
            }
        }

        return roomList;
    }

    // 대기방 참여
    @Override
    public PlayerRes joinRoom(PlayerReq request) {
        Room room = findRoomById(request.roomId());
        if (room.getCurrentPlayers().size() >= room.getMaxPlayers()) {
            throw new RoomException(ExceptionMessage.ROOM_FULL);
        }
        room.getCurrentPlayers().add(request.userId().toString());

        roomRepository.save(room);

        return PlayerRes.from(room);
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

    // 플레이어 삭제
    @Override
    public PlayerRes removePlayer(PlayerReq request) {
        Room room = findRoomById(request.roomId());

        if (!room.getCurrentPlayers().contains(request.userId().toString())) {
            throw new RoomException(ExceptionMessage.USER_NOT_FOUND);
        }

        room.getCurrentPlayers().remove(request.userId().toString());
        room.getPlayerContents().remove(request.userId().toString());

        roomRepository.save(room);

        return PlayerRes.from(room);
    }

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
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomException(ExceptionMessage.ROOM_NOT_FOUND));

        if (room.getPlayerContents() == null) {
            room.setPlayerContents(new HashMap<>());
        }

        return room;
    }

}
