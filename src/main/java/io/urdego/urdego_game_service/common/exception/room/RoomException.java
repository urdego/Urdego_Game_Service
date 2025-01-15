package io.urdego.urdego_game_service.common.exception.room;

import io.urdego.urdego_game_service.common.exception.BaseException;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;

public class RoomException extends BaseException {
    public RoomException(ExceptionMessage message) {
        super(message);
    }
}
