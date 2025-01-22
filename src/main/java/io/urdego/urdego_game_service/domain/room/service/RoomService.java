package io.urdego.urdego_game_service.domain.room.service;

import io.urdego.urdego_game_service.controller.room.dto.request.ContentSelectReq;
import io.urdego.urdego_game_service.controller.room.dto.request.PlayerReq;
import io.urdego.urdego_game_service.controller.room.dto.request.RoomCreateReq;
import io.urdego.urdego_game_service.controller.room.dto.response.PlayerRes;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomCreateRes;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomInfoRes;
import io.urdego.urdego_game_service.common.enums.Status;
import io.urdego.urdego_game_service.domain.room.entity.Room;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomService {

    // 방 생성
    RoomCreateRes createRoom(RoomCreateReq request);

    // 대기방 리스트 조회
    List<RoomInfoRes> getRoomList();

    // 방 참가
    PlayerRes joinRoom(PlayerReq request);

    // 컨텐츠 등록
    void registerContents(ContentSelectReq request);

    // 플레이어 삭제
    PlayerRes removePlayer(PlayerReq request);

    // 방 상태 변경
    Room updateRoomStatusById(String roomId, Status status);

    // 방 정보 조회
    @Transactional(readOnly = true)
    Room findRoomById(String roomId);
}
