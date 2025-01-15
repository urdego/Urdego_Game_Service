package io.urdego.urdego_game_service.common.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String title;

    public BaseException(ExceptionMessage message) {
        super(message.getText());
        this.status = message.getStatus();
        this.title = message.getTitle();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }
}
