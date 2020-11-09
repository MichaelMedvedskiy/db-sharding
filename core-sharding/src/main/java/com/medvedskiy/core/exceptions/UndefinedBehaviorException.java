package com.medvedskiy.core.exceptions;

/**
 * Exception for situations, that require business-requirements specification
 */
public class UndefinedBehaviorException extends Exception {
    public UndefinedBehaviorException(String message) {
        super(message);
    }
}
