package com.deepak.music.common.exception;

import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {
    private final int httpStatus;
    private final String errorCode;
    private final String messageKey;
    private final Object[] messageArgs;

    public ApiException(String messageKey, int httpStatus, String errorCode, Object... messageArgs) {
        super(messageKey);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs == null ? new Object[0] : messageArgs.clone();
    }

}
