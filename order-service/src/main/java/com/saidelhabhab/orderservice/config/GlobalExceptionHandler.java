package com.saidelhabhab.orderservice.config;

import com.saidelhabhab.orderservice.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException ex) {

        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {

        return ResponseEntity
                .status(500)
                .body(new ApiResponse<>(false, "Internal server error", null));
    }
}