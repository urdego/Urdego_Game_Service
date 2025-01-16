package io.urdego.urdego_game_service.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    // 대기방
    ROOM_NOT_FOUND("해당 대기방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "Room not found"),
    ROOM_FULL("해당 대기방의 참여 인원이 초과되었습니다.", HttpStatus.CONFLICT, "Room is Full"),
    CONTENTS_OVER("선택 가능한 컨텐츠의 개수가 초과되었습니다.", HttpStatus.BAD_REQUEST, "Exceeds max content"),

    // 게임,
    GAME_NOT_FOUND("해당 게임을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "Game not found"),
    GAME_ALREADY_COMPLETED("이미 종료된 게임입니다.", HttpStatus.CONFLICT, "Game is already over"),

    // 라운드
    QUESTION_NOT_FOUND("문제를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "Question not found"),

    // 유저
    USER_NOT_FOUND("해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "User not found"),

    // 컨텐츠
    CONTENT_NOT_FOUND("문제 생성을 위한 컨텐츠를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "Content not found");

    private final String text;
    private final HttpStatus status;
    private final String title;
}
