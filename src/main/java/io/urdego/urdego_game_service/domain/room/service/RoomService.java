package io.urdego.urdego_game_service.domain.room.service;

import io.urdego.urdego_game_service.api.room.dto.request.RoomCreateReq;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.room.entity.Room;

import java.util.List;

public interface RoomService {

    // 방 생성
    Room createRoom(RoomCreateReq request);

    // 방 참가
    Room joinRoom(String roomId, Long userId);

    // 컨텐츠 등록
    void registerContents(String roomId, Long userId, List<String> contentIds);

    // component
    // 방 상태 변경
    Room updateRoomStatusById(String roomId, Status status);

    Room findRoomById(String roomId);
}
