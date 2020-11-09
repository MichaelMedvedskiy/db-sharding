package com.medvedskiy.core.exceptions;

public class UnknownSenderIdException extends Exception {
    public UnknownSenderIdException(Long senderId) {
        super(String.format("Sender Id unknown: %d", senderId));
    }
}
