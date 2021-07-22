package com.udacity.jwdnd.course1.cloudstorage.error.exceptions;

public class InvalidValueException extends BusinessException {
    public InvalidValueException(final String message) {
        super(ErrorCode.INVALID_VALUE, message);
    }
}
