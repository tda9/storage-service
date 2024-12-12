package org.example.dastorage.exception;

public class SaveToDatabaseFailedException extends RuntimeException {
    public SaveToDatabaseFailedException(String message) {
        super(message);
    }
}
