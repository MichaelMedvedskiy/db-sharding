package com.medvedskiy.core.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(Throwable cause) {
        super(cause);
    }
}
