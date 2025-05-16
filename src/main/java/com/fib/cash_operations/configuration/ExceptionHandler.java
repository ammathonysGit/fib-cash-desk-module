package com.fib.cash_operations.configuration;

import com.fib.cash_operations.exception.DenominationValidationException;
import com.fib.cash_operations.exception.FileOperationException;
import com.fib.cash_operations.exception.InsufficientBalanceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> violations = new HashMap<>();
        for (FieldError violation : ex.getBindingResult().getFieldErrors()) {
            violations.put(violation.getField(), violation.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(violations);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientBalanceException(InsufficientBalanceException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(FileOperationException.class)
    public ResponseEntity<Map<String, String>> handleCashBalanceReadFailure(FileOperationException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", exception.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DenominationValidationException.class)
    public ResponseEntity<Map<String, String>> handleDenominationValidationException(DenominationValidationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage()));
    }


}
