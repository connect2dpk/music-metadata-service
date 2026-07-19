package com.deepak.music.common.exception;

import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {
    private final int httpStatus;
    private final String errorCode;

    public ApiException(String message, int httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

}
