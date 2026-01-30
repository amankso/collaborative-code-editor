package com.tutorial.projectservice.exception;

import org.springframework.http.HttpStatus;

public class GeneralException extends RuntimeException {

    private final HttpStatus status;

    public GeneralException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    // This is the method the compiler was looking for
    public HttpStatus getStatus() {
        return status;
    }
}