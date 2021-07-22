package com.udacity.jwdnd.course1.cloudstorage.error.exceptions;

public class FileNotUploadedException extends BusinessException {
    public FileNotUploadedException(final String message) {
        super(ErrorCode.FILE_NOT_UPLOADED, message);
    }
}
