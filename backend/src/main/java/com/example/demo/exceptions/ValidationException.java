package com.example.demo.exceptions;

public class ValidationException extends RuntimeException {

    private final String field;
    private final String reason;

    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.reason = message;
    }

    public ValidationException(String field, String reason) {
        super("Validation failed for field '" + field + "': " + reason);
        this.field = field;
        this.reason = reason;
    }

    public String getField() {
        return field;
    }

    public String getReason() {
        return reason;
    }
}
