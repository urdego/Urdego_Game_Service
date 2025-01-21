package io.urdego.urdego_game_service.controller.room;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.urdego.urdego_game_service.controller.room.dto.request.RoomCreateReq;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomCreateRes;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomInfoRes;
import io.urdego.urdego_game_service.domain.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game-service")
public class RoomController {

    private final RoomService roomService;

    // 대기방 생성
    @Tag(name = "대기방 API")
    @Operation(summary = "대기방 생성", description = "방장이 입력한 정보로 대기방 생성")
    @PostMapping("/room/create")
    public ResponseEntity<RoomCreateRes> createRoom(@RequestBody RoomCreateReq request) {
        RoomCreateRes response = roomService.createRoom(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 대기방 목록 조회
    @Tag(name = "대기방 API")
    @Operation(summary = "대기방 목록 조회", description = "현재 생성되어 있는 대기방 목록 전체 조회")
    @GetMapping("/room/list")
    public ResponseEntity<List<RoomInfoRes>> viewRooms() {
        List<RoomInfoRes> response = roomService.getRoomList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
