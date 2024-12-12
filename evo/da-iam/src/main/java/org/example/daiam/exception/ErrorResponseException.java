package org.example.daiam.exception;

public class ErrorResponseException extends RuntimeException{
    public ErrorResponseException(String msg){
        super(msg);
    }
}
