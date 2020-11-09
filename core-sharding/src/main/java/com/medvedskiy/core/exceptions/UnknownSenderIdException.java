package com.medvedskiy.core.exceptions;

/**
 * @see com.medvedskiy.core.services.TotalSumService
 * @deprecated needed if we need to return error on missing senderId in TotalSumService
 */
public class UnknownSenderIdException extends Exception {
    public UnknownSenderIdException(Long senderId) {
        super(String.format("Sender Id unknown: %d", senderId));
    }
}
