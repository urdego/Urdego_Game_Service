package io.urdego.urdego_game_service.api.room;

import io.urdego.urdego_game_service.api.room.dto.request.ContentSelectReq;
import io.urdego.urdego_game_service.api.room.dto.request.PlayerInviteReq;
import io.urdego.urdego_game_service.api.room.dto.request.RoomCreateReq;
import io.urdego.urdego_game_service.api.room.dto.response.RoomCreateRes;
import io.urdego.urdego_game_service.api.room.dto.response.RoomInfoRes;
import io.urdego.urdego_game_service.domain.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    // 대기방 생성
    @MessageMapping("/room/create")
    public void createRoom(RoomCreateReq request) {
        RoomCreateRes response = roomService.createRoom(request);
        messagingTemplate.convertAndSend("/game-service/sub" + response.roomId(), response);
    }

    // 친구 초대
    @MessageMapping("/room/invite")
    public void invitePlayer(PlayerInviteReq request) {
        RoomInfoRes response = roomService.joinRoom(request.roomId(), request.userId());
        messagingTemplate.convertAndSend("/game-service/sub" + request.roomId(), response);
    }

    // 컨텐츠 선택 (최대 5개, 0개일 경우 자체 컨텐츠 제공)
    @MessageMapping("/room/select-content")
    public void selectContent(ContentSelectReq request) {
        roomService.registerContents(request);
    }

    // 준비 상태


}
