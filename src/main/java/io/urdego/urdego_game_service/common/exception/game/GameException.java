package io.urdego.urdego_game_service.common.exception.game;

import io.urdego.urdego_game_service.common.exception.BaseException;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;

public class GameException extends BaseException {
    public GameException(ExceptionMessage message) {
        super(message);
    }
}
