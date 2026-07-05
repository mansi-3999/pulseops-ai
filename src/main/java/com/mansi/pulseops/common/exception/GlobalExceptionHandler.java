package com.mansi.pulseops.common.exception;

import com.mansi.pulseops.common.api.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IncidentNotFoundException.class)
    ResponseEntity<ApiError> notFound(
            IncidentNotFoundException ex,
            HttpServletRequest req) {

        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                req.getRequestURI(),
                Map.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(
            MethodArgumentNotValidException ex,
            HttpServletRequest req) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return build(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                req.getRequestURI(),
                errors
        );
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(
            Exception ex,
            HttpServletRequest req) {

        log.error(
                "Unexpected error while processing request path={}",
                req.getRequestURI(),
                ex
        );

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                req.getRequestURI(),
                Map.of()
        );
    }

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> validationErrors) {

        return ResponseEntity
                .status(status)
                .body(
                        new ApiError(
                                OffsetDateTime.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                message,
                                path,
                                validationErrors
                        )
                );
    }
}