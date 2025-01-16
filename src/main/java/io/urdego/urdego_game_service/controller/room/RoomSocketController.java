package io.urdego.urdego_game_service.controller.room;

import io.urdego.urdego_game_service.controller.room.dto.request.ContentSelectReq;
import io.urdego.urdego_game_service.controller.room.dto.request.PlayerInviteReq;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomInfoRes;
import io.urdego.urdego_game_service.domain.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoomSocketController {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

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
