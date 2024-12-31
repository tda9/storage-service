//package org.example.daiam.exception;
//
//
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.ConstraintViolationException;
//import jakarta.validation.UnexpectedTypeException;
//import jakarta.ws.rs.BadRequestException;
//import jakarta.ws.rs.InternalServerErrorException;
//import jakarta.ws.rs.NotFoundException;
//import lombok.extern.slf4j.Slf4j;
//import org.example.model.dto.response.Response;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//@Slf4j
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    //AuthorizationDeniedException bo sung them
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<Response<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
//        log.error(ex.getMessage());
//        return ResponseEntity.status(400).body(Response.badRequest(ex.getMessage(), null));
//    }
//
//    @ExceptionHandler(InternalServerErrorException.class)
//    public ResponseEntity<Response<?>> handleInternalException(InternalServerErrorException ex) {
//        log.error(ex.getMessage());
//        return ResponseEntity.status(500).body(Response.fail(ex.getMessage(), null));
//    }
//
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<Response<?>> handleBadRequestException(BadRequestException ex) {
//        log.error(ex.getMessage());
//        return ResponseEntity.status(400).body(Response.badRequest(ex.getMessage(), null));
//    }
//
//    @ExceptionHandler(NotFoundException.class)
//    public ResponseEntity<Response<?>> handleNotfoundException(NotFoundException ex) {
//        log.error(ex.getMessage());
//        return ResponseEntity.status(404).body(Response.notFound(ex.getMessage(), null));
//    }
//
//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<Response<?>> handleUserNotFound(UserNotFoundException ex) {
//        log.error(ex.getMessage());
//        return ResponseEntity.status(404).body(Response.badRequest(ex.getMessage(), ex));
//    }
//
//    @ExceptionHandler(ErrorResponseException.class)
//    public ResponseEntity<?> handleErrorResponseException(ErrorResponseException ex) {
//        log.error(ex.getMessage());
//        return ResponseEntity.status(400).body(Response.fail(ex.getMessage(), ex));
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        StringBuilder errorMessages = new StringBuilder();
//        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
//            errorMessages.append("At field ").append(error.getField()).append(": ").append(error.getDefaultMessage()).append(System.lineSeparator());
//        }
//        log.error(ex.getMessage());
//        return ResponseEntity.status(400).body(Response.badRequest(errorMessages.toString(), null));
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<?> handleConstraintViolationExceptions(ConstraintViolationException ex) {
//        StringBuilder errorMessages = new StringBuilder();
//        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
//            errorMessages.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append(System.lineSeparator());
//        }
//        log.error(ex.getMessage());
//        return ResponseEntity.status(400).body(Response.fail(errorMessages.toString(), null));
//    }
//
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<?> handleErrorResponseException(HttpMessageNotReadableException ex) {
//        log.error(ex.getMessage());
//        return ResponseEntity.status(400).body(Response.fail(ex.getMessage(), null));
//    }
//
//    @ExceptionHandler(UnexpectedTypeException.class)
//    public ResponseEntity<?> handleWrongTypeException(UnexpectedTypeException ex) {
//        log.error(ex.getMessage());
//        return ResponseEntity.status(400).body(Response.fail(ex.getMessage(), ex));
//    }
//
//    @ExceptionHandler(NullPointerException.class)
//    public ResponseEntity<?> handleNullException(NullPointerException ex) {
//        log.error(ex.getMessage());
//        return ResponseEntity.status(400).body(Response.fail(ex.getMessage(), ex));
//    }
//
//}
