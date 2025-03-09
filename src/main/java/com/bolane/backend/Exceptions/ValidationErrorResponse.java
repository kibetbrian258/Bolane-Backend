package com.bolane.backend.Exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class ValidationErrorResponse extends ErrorResponse{
    private Map<String, String> errors;

    public ValidationErrorResponse(
            int status,
            String message,
            LocalDateTime timestamp,
            Map<String, String> errors) {
        super(status, message, timestamp);
        this.errors = errors;
    }
}
