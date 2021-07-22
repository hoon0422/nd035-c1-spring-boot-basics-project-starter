package com.udacity.jwdnd.course1.cloudstorage.error.exceptions;

public class FileNotDownloadedException extends BusinessException {
    public FileNotDownloadedException(final String message) {
        super(ErrorCode.FILE_NOT_DOWNLOADED, message);
    }
}
