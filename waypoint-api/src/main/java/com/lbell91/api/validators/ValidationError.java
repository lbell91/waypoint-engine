package com.lbell91.api.validators;

public record ValidationError(String errorCode, String errorMessage) {
    
    public static ValidationError of(ValidationErrorType type) {
        return new ValidationError(type.code(), type.message());
    }

    public String code() {
        return errorCode;
    }

    public String message() {
        return errorMessage;
    }
}
