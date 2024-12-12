package org.example.daiam.exception;

public class SaveToDatabaseFailedException extends RuntimeException {
    public SaveToDatabaseFailedException(String message) {
        super(message);
    }
}
