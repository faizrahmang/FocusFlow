package com.todoapp.exception;

public class PhotoTooLargeException extends RuntimeException {
    public PhotoTooLargeException(String message) {
        super(message);
    }
}
