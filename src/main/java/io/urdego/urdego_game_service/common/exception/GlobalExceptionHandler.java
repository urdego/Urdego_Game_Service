package io.urdego.urdego_game_service.common.exception;

import feign.FeignException;
import io.urdego.urdego_game_service.common.exception.game.GameException;
import io.urdego.urdego_game_service.common.exception.room.RoomException;
import io.urdego.urdego_game_service.common.exception.round.QuestionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * BaseException 처리 핸들러
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e, WebRequest request) {
        logger.warn("{} : {}", e.getTitle(), e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.from(
                e.getStatus().value(),
                e.getTitle(),
                e.getMessage()
        );

        return new ResponseEntity<>(errorResponse, e.getStatus());
    }

    /**
     * 모든 기타 예외 처리 핸들러
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse =
                ErrorResponse.from(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * FeignException 처리 핸들러
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException e, WebRequest request) {
        HttpStatus status = HttpStatus.resolve(e.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        if (status.is4xxClientError()) {
            logger.warn("FeignException - {} : {}", e.status(), e.getMessage());
        } else {
            logger.error("FeignException - {} : {}", e.status(), e.getMessage(), e);
        }

        ErrorResponse errorResponse = ErrorResponse.from(
                e.status(),
                status.getReasonPhrase(),
                e.getMessage()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
}
