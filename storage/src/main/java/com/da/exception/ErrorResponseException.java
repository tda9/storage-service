package com.da.exception;

public class ErrorResponseException extends RuntimeException{
    public ErrorResponseException(String msg){
        super(msg);
    }
}
