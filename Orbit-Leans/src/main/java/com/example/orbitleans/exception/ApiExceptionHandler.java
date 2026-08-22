package com.example.orbitleans.exception;

public class ApiExceptionHandler extends RuntimeException {
    public ApiExceptionHandler(String message) {
        super(message);
    }
}
