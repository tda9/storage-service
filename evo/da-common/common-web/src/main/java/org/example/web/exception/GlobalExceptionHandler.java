package org.example.web.exception;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;

import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.example.model.dto.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    //AuthorizationDeniedException bo sung them
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(400).body(Response.badRequest(ex.getMessage(), null));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Response<?>> handleIllegalArgumentException(ForbiddenException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(403).body(Response.forbidden(ex.getMessage(), null));
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Response<?>> handleTooManyRequestException(HttpClientErrorException.TooManyRequests ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(429).body(Response.fail(ex.getMessage(), 429, null));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response<?>> handleIllegalArgumentException(BadCredentialsException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(401).body(Response.unAuthorize(ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    public ResponseEntity<Response<?>> handleInvalidTokenException(InvalidBearerTokenException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(401).body(Response.unAuthorize(ex.getMessage(), null));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<Response<?>> handleInternalException(InternalServerErrorException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(500).body(Response.fail(ex.getMessage(), ex));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Response<?>> handleBadRequestException(BadRequestException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(400).body(Response.badRequest(ex.getMessage(), Arrays.stream(ex.getStackTrace()).findFirst()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response<?>> handleNotfoundException(NotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(404).body(Response.notFound(ex.getMessage(), Arrays.stream(ex.getStackTrace()).findFirst()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessages = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessages.append("At field ").append(error.getField()).append(": ").append(error.getDefaultMessage()).append(System.lineSeparator());
        }
        log.error(ex.getMessage());
        return ResponseEntity.status(400).body(Response.badRequest(errorMessages.toString(), null));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        StringBuilder errorMessages = new StringBuilder();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errorMessages.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append(System.lineSeparator());
        }
        log.error(ex.getMessage());
        return ResponseEntity.status(400).body(Response.fail(errorMessages.toString(), null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleErrorResponseException(HttpMessageNotReadableException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(400).body(Response.fail(ex.getMessage(), null));
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<?> handleWrongTypeException(UnexpectedTypeException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(400).body(Response.fail(ex.getMessage(), ex));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullException(NullPointerException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(400).body(Response.fail(ex.getMessage(), ex));
    }

//    @ExceptionHandler(ResponseStatusException.class)
//    public ResponseEntity<Response<?>> handleResponseStatusException(ResponseStatusException ex) {
//        log.error("Exception caught: {}", ex.getMessage());
//        return ResponseEntity
//                .status(ex.getStatusCode())
//                .body(Response.badRequest(ex.getReason(), null));
//    }
}
