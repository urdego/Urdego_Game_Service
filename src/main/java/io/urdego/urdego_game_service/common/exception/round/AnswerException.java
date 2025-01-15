package io.urdego.urdego_game_service.common.exception.round;

import io.urdego.urdego_game_service.common.exception.BaseException;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;

public class AnswerException extends BaseException {
    public AnswerException(ExceptionMessage message) {
        super(message);
    }
}
