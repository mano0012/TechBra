package com.techbra.bff.web.exception;

import com.techbra.bff.domain.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum ErrorType {
    DOMAIN_ERROR,
    VALIDATION_ERROR,
    UNKNOWN_ERROR;
}

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Domain Error
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        ErrorResponse error = new ErrorResponse(
                ErrorType.DOMAIN_ERROR.name(),
                ex.getMessage(),
                LocalDateTime.now());

        log.error("Status: {} - Message: {}", ex.getStatusCode(), ex.getMessage(), ex);

        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    // Validation Body Error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse error = new ErrorResponse(
                ErrorType.VALIDATION_ERROR.name(),
                "Dados inválidos: " + errors.toString(),
                LocalDateTime.now());

        log.error(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Validation Path Error
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(jakarta.validation.ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().stream()
                .forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));

        ErrorResponse error = new ErrorResponse(
                ErrorType.VALIDATION_ERROR.name(),
                "Dados inválidos: " + errors.toString(),
                LocalDateTime.now());

        log.error(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Validation Malformed Json Error
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(
                ErrorType.VALIDATION_ERROR.name(),
                "Corpo da requisição não é um JSON válido.",
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Unknown Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                ErrorType.UNKNOWN_ERROR.name(),
                "Erro interno do servidor",
                LocalDateTime.now());

        log.error(ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    public static class ErrorResponse {
        private String code;
        private String message;
        private LocalDateTime timestamp;

        public ErrorResponse(String code, String message, LocalDateTime timestamp) {
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}