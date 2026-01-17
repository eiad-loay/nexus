package com.nexus.ecommerce.exception;

import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.exception.custom.EntityNotFoundException;
import com.nexus.ecommerce.exception.custom.UserAlreadyExistException;
import com.nexus.ecommerce.exception.custom.UserNotEnabledException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import javax.security.sasl.AuthenticationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response<Object>> handleAuthenticationException(AuthenticationException e) {
        log.warn("AuthenticationException handled: {}", e.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return new ResponseEntity<>(buildResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), errors), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotEnabledException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response<Object> handleNotEnabledException(UserNotEnabledException e) {
        log.warn("UserNotEnabledException handled: {}", e.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
        log.warn("Validation failed with {} error(s)", errors.size());
        return new ResponseEntity<>(buildResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response<Object>> handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn("EntityNotFoundException handled: {}", e.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return new ResponseEntity<>(buildResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), errors), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Response<Object>> handleUserAlreadyExistException(UserAlreadyExistException e) {
        log.warn("UserAlreadyExistException handled: {}", e.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return new ResponseEntity<>(buildResponse(HttpStatus.CONFLICT.value(),  HttpStatus.CONFLICT.getReasonPhrase(), errors), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response<Object>> handleException(Exception e) {
        log.error("Unexpected exception handled", e);
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return new ResponseEntity<>(buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),  HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), errors), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response<Object>> handleResourceNotFound(NoResourceFoundException e) {
        log.error("NoResourceFoundException handled");
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return new ResponseEntity<>(buildResponse(HttpStatus.NOT_FOUND.value(),  HttpStatus.NOT_FOUND.getReasonPhrase(), errors), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response<Object>> handleJwtEmailValidationException(JwtException e) {
        log.warn("JwtException handled: {}", e.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return new ResponseEntity<>(buildResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException handled: {}", e.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return new ResponseEntity<>(buildResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), errors), HttpStatus.BAD_REQUEST);
    }

    private Response<Object> buildResponse(int status, String message, List<String> errorList) {
        return Response.builder()
                .status(status)
                .message(message)
                .errorList(errorList)
                .build();
    }
}
