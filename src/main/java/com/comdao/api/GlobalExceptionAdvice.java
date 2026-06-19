package com.comdao.api;

import com.comdao.api.base.BaseExceptionAdvice;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvice implements BaseExceptionAdvice {
    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationFail(HttpServletRequest request, MethodArgumentNotValidException e) {
        HashMap<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) ->
        {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Validation Failure", errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(HttpServletRequest request, AccessDeniedException e) {
        return buildErrorResponse(request, e, HttpStatus.UNAUTHORIZED, "Unauthorized", null);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFormat(HttpServletRequest request, InvalidFormatException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Bad Request", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgs(HttpServletRequest request, IllegalArgumentException e) {
        e.printStackTrace();
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Illegal arguments", null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(HttpServletRequest request, IllegalStateException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Illegal arguments", null);
    }

//    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
//    public ResponseEntity<Map<String, Object>> handlePreAuthFailure(HttpServletRequest request, org.springframework.security.access.AccessDeniedException e) {
//        return buildErrorResponse(request, e, HttpStatus.UNAUTHORIZED, "Access denied", null);
//    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, Object>> handleGeneral(HttpServletRequest request, Exception e) {
//        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Error", null);
//    }
}
