package io.urdego.urdego_game_service.common.exception.player;

import io.urdego.urdego_game_service.common.exception.BaseException;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;

public class PlayerException extends BaseException {
    public PlayerException(ExceptionMessage message) {
        super(message);
    }
}
