package com.deepak.music.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class for optimistic locking failures (version conflicts).
 * Used when concurrent modifications conflict.
 */
public class OptimisticLockingException extends ApiException {

    public OptimisticLockingException(String messageKey) {
        super(messageKey, HttpStatus.CONFLICT.value(), "CONFLICT");
    }

    public OptimisticLockingException() {
        super("optimistic.lock.conflict", HttpStatus.CONFLICT.value(), "CONFLICT");
    }
}

