package io.urdego.urdego_game_service.controller.room;

import io.urdego.urdego_game_service.domain.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoomSocketController {

    private final RoomService roomService;

//    // 친구 초대
//    @MessageMapping("/room/player/invite")
//    public void invitePlayer(PlayerReq request) {
//        PlayerRes response = roomService.joinRoom(request);
//        messagingTemplate.convertAndSend("/game-service/sub/" + response.roomId(), response);
//    }
//
//    // 컨텐츠 선택 (최대 5개, 0개일 경우 자체 컨텐츠 제공)
//    @MessageMapping("/room/select-content")
//    public void selectContent(ContentSelectReq request) {
//        roomService.registerContents(request);
//    }
//
//    // 플레이어 삭제
//    @MessageMapping("/room/player/remove")
//    public void removePlayer(PlayerReq request) {
//        PlayerRes response = roomService.removePlayer(request);
//        messagingTemplate.convertAndSend("/game-service/sub/" + response.roomId(), response);
//    }
}
