package com.udacity.jwdnd.course1.cloudstorage.error;

import com.udacity.jwdnd.course1.cloudstorage.error.exceptions.ErrorCode;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ErrorResponse {
    private final String message;
    private final Integer status;
    private final String code;
    private final List<FieldError> errors;

    public ErrorResponse(final ErrorCode code, final List<FieldError> errors) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.code = code.getCode();
        this.errors = errors;
    }

    public ErrorResponse(final ErrorCode code) {
        this.message = code.getMessage();
        this.status = code.getStatus();
        this.code = code.getCode();
        this.errors = new ArrayList<>();
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public String getCode() {
        return code;
    }

    public static ErrorResponse of(final ErrorCode code,
                                   final BindingResult bindingResult) {
        return new ErrorResponse(code, FieldError.of(bindingResult));
    }

    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final ErrorCode code,
                                   final List<FieldError> errors) {
        return new ErrorResponse(code, errors);
    }

    public static ErrorResponse of(
            final MethodArgumentTypeMismatchException e) {
        final String value =
                e.getValue() == null ? "" : e.getValue().toString();
        final List<FieldError> errors =
                FieldError.of(e.getName(), value, e.getErrorCode());
        return new ErrorResponse(ErrorCode.INVALID_TYPE, errors);
    }

    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;

        public FieldError(final String field, final String value,
                          final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(final String field,
                                          final String value,
                                          final String reason) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError(field, value, reason));
            return errors;
        }

        public static List<FieldError> of(final BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(
                            e -> new FieldError(e.getField(),
                                    e.getRejectedValue() == null
                                            ? ""
                                            : e.getRejectedValue().toString(),
                                    e.getDefaultMessage()))
                    .collect(Collectors.toList());
        }

        public String getField() {
            return field;
        }

        public String getValue() {
            return value;
        }

        public String getReason() {
            return reason;
        }
    }
}
