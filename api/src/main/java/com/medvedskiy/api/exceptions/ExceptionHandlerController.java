package com.medvedskiy.api.exceptions;

import com.medvedskiy.core.exceptions.BadRequestException;
import com.medvedskiy.core.exceptions.UndefinedBehaviorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
    Logger log = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(UndefinedBehaviorException.class)
    public ResponseEntity<Error> handleNotFoundException(Exception e) {
        log.warn("UndefinedBehaviorException", e);

        return
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), e.getMessage())
                        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Error> handleBadRequestException(Exception e) {
        log.warn("BadRequestException", e);

        return
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                                new Error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e.getCause().getMessage())

                        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception e) {
        log.error("INTERNAL_SERVER_ERROR", e);
        return
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), e.getMessage())
                        );
    }

}
