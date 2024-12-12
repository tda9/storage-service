package org.example.dastorage.exception;

public class ErrorResponseException extends RuntimeException{
    public ErrorResponseException(String msg){
        super(msg);
    }
}
