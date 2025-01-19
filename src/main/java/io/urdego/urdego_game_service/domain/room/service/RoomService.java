package io.urdego.urdego_game_service.domain.room.service;

import io.urdego.urdego_game_service.controller.room.dto.request.ContentSelectReq;
import io.urdego.urdego_game_service.controller.room.dto.request.PlayerInviteReq;
import io.urdego.urdego_game_service.controller.room.dto.request.RoomCreateReq;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomCreateRes;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomInfoRes;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.room.entity.Room;
import org.springframework.transaction.annotation.Transactional;

public interface RoomService {

    // 방 생성
    RoomCreateRes createRoom(RoomCreateReq request);

    // 방 참가
    RoomInfoRes joinRoom(PlayerInviteReq request);

    // 컨텐츠 등록
    void registerContents(ContentSelectReq request);

    // 방 상태 변경
    Room updateRoomStatusById(String roomId, Status status);

    // 방 정보 조회
    @Transactional(readOnly = true)
    Room findRoomById(String roomId);
}
