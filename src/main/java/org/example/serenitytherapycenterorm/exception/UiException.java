package org.example.serenitytherapycenterorm.exception;

public class UiException extends RuntimeException {
    public UiException(String message) {
        super(message);
    }
    public UiException(String message, Throwable cause) {
        super(message, cause);
    }
}
