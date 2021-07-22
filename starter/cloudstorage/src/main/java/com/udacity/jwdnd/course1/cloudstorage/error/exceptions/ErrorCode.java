package com.udacity.jwdnd.course1.cloudstorage.error.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    // Common
    INTERNAL_SERVER_ERROR(500, "C000", "Internal Server Error"),
    ENTITY_NOT_FOUND(404, "C001", "Entity Not Found"),
    INVALID_VALUE(400, "C002", "Invalid Value"),
    INVALID_TYPE(400, "C003", "Invalid Type"),
    METHOD_NOT_ALLOWED(405, "C004", "Method Not Allowed"),
    ACCESS_DENIED(403, "C005", "Access is Denied"),

    // Files
    FILE_NOT_UPLOADED(409, "F001", "File Not Uploaded"),
    FILE_NOT_DOWNLOADED(409, "F002", "File Not Downloaded");

    private final String code;
    private final String message;
    private final Integer status;

    ErrorCode(final Integer status, final String code, final String message) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }
}
