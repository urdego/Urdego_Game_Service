package io.urdego.urdego_game_service.common.exception.round;

import io.urdego.urdego_game_service.common.exception.BaseException;
import io.urdego.urdego_game_service.common.exception.ExceptionMessage;

public class QuestionException extends BaseException {
    public QuestionException(ExceptionMessage message) {
        super(message);
    }
}
