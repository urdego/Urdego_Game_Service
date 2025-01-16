package io.urdego.urdego_game_service.api.room;

import io.urdego.urdego_game_service.api.room.dto.request.RoomCreateReq;
import io.urdego.urdego_game_service.api.room.dto.response.RoomCreateRes;
import io.urdego.urdego_game_service.domain.room.service.RoomService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game-service")
public class RoomController {

    private final RoomService roomService;

    // 대기방 생성
    @PostMapping("/room/create")
    public ResponseEntity<RoomCreateRes> createRoom(@RequestBody RoomCreateReq request) {
        RoomCreateRes response = roomService.createRoom(request);
        return ResponseEntity.ok().body(response);
    }

}
