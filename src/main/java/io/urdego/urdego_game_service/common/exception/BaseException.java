package io.urdego.urdego_game_service.common.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String title;
    private final String additionalMessage;

    public BaseException(ExceptionMessage message) {
        super(message.getText());
        this.status = message.getStatus();
        this.title = message.getTitle();
        this.additionalMessage = "";
    }

    public BaseException(ExceptionMessage message, String additionalMessage) {
        super(message.getText() + " | " + additionalMessage);
        this.status = message.getStatus();
        this.title = message.getTitle();
        this.additionalMessage = additionalMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getAdditionalMessage() {
        return additionalMessage;
    }
}
