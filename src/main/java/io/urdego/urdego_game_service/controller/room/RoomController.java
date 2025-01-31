package io.urdego.urdego_game_service.controller.room;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.urdego.urdego_game_service.controller.room.dto.request.ContentSelectReq;
import io.urdego.urdego_game_service.controller.room.dto.request.PlayerReq;
import io.urdego.urdego_game_service.controller.room.dto.request.RoomCreateReq;
import io.urdego.urdego_game_service.controller.room.dto.response.RoomPlayersRes;
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
@RequestMapping("/api/game-service/room")
public class RoomController {

    private final RoomService roomService;

    @Tag(name = "대기방 API")
    @Operation(summary = "대기방 생성", description = "방장이 입력한 정보로 대기방 생성")
    @PostMapping("/create")
    public ResponseEntity<RoomCreateRes> createRoom(@RequestBody RoomCreateReq request) {
        RoomCreateRes response = roomService.createRoom(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Tag(name = "대기방 API")
    @Operation(summary = "대기방 목록 조회", description = "현재 생성되어 있는 대기방 목록 전체 조회")
    @GetMapping("/list")
    public ResponseEntity<List<RoomInfoRes>> viewRooms() {
        List<RoomInfoRes> response = roomService.getRoomList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "대기방 Socket")
    @Operation(summary = "플레이어 초대", description = "대기방에 플레이어 참여")
    @PostMapping("/player/invite")
    public ResponseEntity<RoomPlayersRes> invitePlayer(@RequestBody PlayerReq request) {
        RoomPlayersRes response = roomService.joinRoom(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "대기방 Socket")
    @Operation(summary = "플레이어 삭제", description = "대기방에 플레이어 삭제")
    @PostMapping("/player/remove")
    public ResponseEntity<RoomPlayersRes> removePlayer(@RequestBody PlayerReq request) {
        RoomPlayersRes response = roomService.removePlayer(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "대기방 Socket")
    @Operation(summary = "게임 컨텐츠 선택", description = "게임 문제 출제를 위한 컨텐츠 선택")
    @PostMapping("/select-content")
    public ResponseEntity<Void> selectContent(@RequestBody ContentSelectReq request) {
        roomService.registerContents(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
