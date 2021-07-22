package com.udacity.jwdnd.course1.cloudstorage.error.exceptions.entity;

import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.BusinessException;
import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.ErrorCode;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(final String message) {
        super(ErrorCode.ENTITY_NOT_FOUND, message);
    }
}
