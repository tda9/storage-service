package org.example.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;


@Getter
@Setter
@SuperBuilder
public class BasedResponse<T> implements Serializable {
    private boolean requestStatus;
    private int httpStatusCode;
    @Builder.Default
    private long timestamp = Instant.now().toEpochMilli();
    private String message;
    private T data;
    @JsonIgnore
    private Exception exception;
public BasedResponse(T data){
    this.timestamp= Instant.now().toEpochMilli();
    this.httpStatusCode=200;
    this.message = "Success";
    this.requestStatus = true;
    this.data = data;
}
public static <T> BasedResponse<T> fail(String message, Exception ex) {
    return BasedResponse.<T>builder()
            .exception(ex)
            .httpStatusCode(500)
            .message(message)
            .requestStatus(false)
            .build();
}

    public static <T> BasedResponse<T> success(String message, T data) {
        return BasedResponse.<T>builder()
                .httpStatusCode(200)
                .message(message)
                .requestStatus(true)
                .data(data)
                .build();
    }

    public static <T> BasedResponse<T> created(String message, T data) {
        return BasedResponse.<T>builder()
                .httpStatusCode(201)
                .message(message)
                .requestStatus(true)
                .data(data)
                .build();
    }

    public static <T> BasedResponse<T> badRequest(String message, T data) {
        return BasedResponse.<T>builder()
                .httpStatusCode(400)
                .message(message)
                .requestStatus(false)
                .data(data)
                .build();
    }

    public static <T> BasedResponse<T> notFound(String message, T data) {
        return BasedResponse.<T>builder()
                .httpStatusCode(404)
                .message(message)
                .requestStatus(false)
                .data(data)
                .build();
    }

    //    public static <T> BasedResponse<T> fail(String message, Exception ex){
//        BasedResponse<T> response = new BasedResponse<>();
//        response.setException(ex);
//        response.setHttpStatusCode(500);
//        response.setMessage(message);
//        response.setRequestStatus(false);
//        return response;
//    }
//
//    public static<T> BasedResponse<T> success(String message, T data){
//        BasedResponse<T> response = new BasedResponse<>();
//        response.setHttpStatusCode(200);
//        response.setMessage(message);
//        response.setRequestStatus(true);
//        response.setData(data);
//        return response;
//    }
//    public static<T> BasedResponse<T> created(String message, T data){
//        BasedResponse<T> response = new BasedResponse<>();
//        response.setRequestStatus(true);
//        response.setHttpStatusCode(201);
//        response.setMessage(message);
//        response.setRequestStatus(true);
//        response.setData(data);
//        return response;
//    }
//    public static<T> BasedResponse<T> badRequest(String message,T data){
//        BasedResponse<T> response = new BasedResponse<>();
//        response.setRequestStatus(false);
//        response.setHttpStatusCode(400);
//        response.setMessage(message);
//        response.setData(data);
//        return response;
//    }
//    public static<T> BasedResponse<T> notFound(String message,T data){
//        BasedResponse<T> response = new BasedResponse<>();
//        response.setRequestStatus(false);
//        response.setHttpStatusCode(404);
//        response.setMessage(message);
//        response.setData(data);
//        return response;
//    }
}
