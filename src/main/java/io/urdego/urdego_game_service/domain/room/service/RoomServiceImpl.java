package io.urdego.urdego_game_service.domain.room.service;

import io.urdego.urdego_game_service.common.exception.player.PlayerException;
import io.urdego.urdego_game_service.controller.room.dto.request.ContentSelectReq;
import io.urdego.urdego_game_service.controller.room.dto.request.PlayerReq;
import io.urdego.urdego_game_service.controller.room.dto.request.RoomCreateReq;
import io.urdego.urdego_game_service.controller.room.dto.response.PlayerRes;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomPlayersRes;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomCreateRes;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomInfoRes;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;
import io.urdego.urdego_game_service.common.exception.room.RoomException;
import io.urdego.urdego_game_service.domain.player.entity.Player;
import io.urdego.urdego_game_service.domain.player.service.PlayerService;
import io.urdego.urdego_game_service.domain.room.entity.Room;
import io.urdego.urdego_game_service.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final PlayerService playerService;

    // 대기방 생성
    @Override
    public RoomCreateRes createRoom(RoomCreateReq request) {
        String roomName = (request.roomName() == null || request.roomName().isBlank())
                ? "어데고 게임방" : request.roomName();

        Room room = Room.builder()
                .roomId(UUID.randomUUID().toString())
                .roomName(roomName)
                .status(Status.WAITING)
                .maxPlayers(request.maxPlayers())
                .totalRounds(request.totalRounds())
                .currentPlayers(new ArrayList<>())
                .playerContents(new HashMap<>())
                .readyStatus(new HashMap<>())
                .build();

        roomRepository.save(room);
        log.info("대기방 생성 | roomId: {}", room.getRoomId());

        return RoomCreateRes.from(room);
    }

    // 대기방 리스트 조회
    @Override
    public List<RoomInfoRes> getRoomList() {
        List<Room> roomList = StreamSupport.stream(roomRepository.findAll().spliterator(), false)
                .filter(Objects::nonNull)
                .toList();

        if (roomList.isEmpty()) {
            log.warn("대기방이 없습니다. 빈 리스트를 반환합니다.");
            return List.of();
        }

        List<Long> hostIds = roomList.stream()
                .map(Room::getHostId)
                .distinct()
                .toList();

       Map<Long, Player> hostMap = hostIds.stream()
               .map(playerService::getPlayer)
               .filter(Objects::nonNull)
               .collect(Collectors.toMap(Player::getUserId, player -> player));

        List<RoomInfoRes> roomInfoList = roomList.stream()
                        .map(room -> {
                            Player hostInfo = hostMap.get(room.getHostId());
                            if (hostInfo == null) {
                                throw new PlayerException(ExceptionMessage.USER_NOT_FOUND, "hostId: " + room.getHostId());
                            }

                            return RoomInfoRes.from(room, PlayerRes.from(hostInfo));
                        })
                        .toList();

        log.info("대기방 조회 | {}개", roomInfoList.size());

        return roomInfoList;
    }

    // 플레이어 참여
    @Override
    public RoomPlayersRes joinRoom(PlayerReq request) {
        Room room = findRoomById(request.roomId());
        Long user = request.userId();

        if (room.getCurrentPlayers().size() >= room.getMaxPlayers()) {
            throw new RoomException(ExceptionMessage.ROOM_FULL, "roomId: " + request.roomId());
        }

        if (room.getHostId() == null) {
            room.setHostId(user);
            log.info("방장 설정 | roomId: {}, hostId: {}", request.roomId(), user);
        }

        room.getCurrentPlayers().add(user);
        room.getReadyStatus().put(user, false);

        roomRepository.save(room);
        log.info("대기방 참여 | roomId: {}, currentPlayers: {}", request.roomId(), room.getCurrentPlayers());

        return getCurrentPlayersInfo(room);
    }

    // 플레이어 삭제
    @Override
    public RoomPlayersRes removePlayer(PlayerReq request) {
        Room room = findRoomById(request.roomId());
        Long user = request.userId();

        if (!room.getCurrentPlayers().contains(user)) {
            throw new RoomException(ExceptionMessage.USER_NOT_FOUND, "userId: " + request.userId());
        }
        room.getCurrentPlayers().remove(user);
        room.getPlayerContents().remove(user);
        room.getReadyStatus().remove(user);

        if (room.getHostId().equals(user)) {
            if (!room.getCurrentPlayers().isEmpty()) {
                room.setHostId(room.getCurrentPlayers().get(0));
                log.info("방장 변경 | roomId: {}, newHost: {}", room.getRoomId(), room.getHostId());
            } else {
                roomRepository.delete(room);
                log.info("방 삭제됨 | roomId: {}", room.getRoomId());
                return null;
            }
        }

        roomRepository.save(room);
        log.info("대기방 플레이어 삭제 | roomId: {}, currentPlayers: {}", request.roomId(), room.getCurrentPlayers());

        return getCurrentPlayersInfo(room);
    }

    // 플레이어 준비
    @Override
    public RoomPlayersRes readyPlayer(PlayerReq request) {
        Room room = findRoomById(request.roomId());

        room.getReadyStatus().put(request.userId(), request.isReady());
        roomRepository.save(room);

        return getCurrentPlayersInfo(room);
    }

    // 컨텐츠 등록
    @Override
    public void registerContents(ContentSelectReq request) {
        Room room = findRoomById(request.roomId());
        if (request.contentIds().size() > 5) {
            throw new RoomException(ExceptionMessage.CONTENTS_OVER);
        }

        room.getPlayerContents().put(request.userId(), request.contentIds());
        roomRepository.save(room);
        log.info("컨텐츠 등록됨 | userId: {}, contentIds:{}", request.userId(), request.contentIds());
    }

    // 대기방 상태 변경
    @Override
    public Room updateRoomStatusById(String roomId, Status status) {
        Room room = findRoomById(roomId);
        room.setStatus(status);

        roomRepository.save(room);
        log.info("대기방 상태 변경 | roomId: {}, Status: {}", room.getRoomId(), room.getStatus());

        return room;
    }

    // 방 정보 조회
    @Override
    @Transactional(readOnly = true)
    public Room findRoomById(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomException(ExceptionMessage.ROOM_NOT_FOUND));

        if (room.getCurrentPlayers() == null) {
            room.setCurrentPlayers(new ArrayList<>());
        }

        if (room.getReadyStatus() == null) {
            room.setReadyStatus(new HashMap<>());
        }

        if (room.getPlayerContents() == null) {
            room.setPlayerContents(new HashMap<>());
        }

        return room;
    }

    // 방 플레이어 정보 불러오기
    private RoomPlayersRes getCurrentPlayersInfo(Room room) {
        List<Player> players = room.getCurrentPlayers().stream()
                .map(playerService::getPlayer)
                .toList();

        Map<Long, String> userIdToNickname = players.stream()
                .collect(Collectors.toMap(Player::getUserId, Player::getNickname));

        List<PlayerRes> currentPlayers = players.stream()
                .map(PlayerRes::from)
                .toList();

        String host = userIdToNickname.get(room.getHostId());

        Map<String, Boolean> readyStatus = room.getReadyStatus().entrySet().stream()
                .collect(Collectors.toMap(entry -> userIdToNickname.get(entry.getKey()), Map.Entry::getValue));

        boolean allReady = players.stream()
                .filter(player -> !player.getUserId().equals(room.getHostId()))
                .allMatch(player -> room.getReadyStatus().getOrDefault(player.getUserId(), false));

        return RoomPlayersRes.from(room, currentPlayers, host, readyStatus, allReady);
    }
}
