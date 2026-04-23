package de.dhbwravensburg.webeng.stagefinder.api.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
