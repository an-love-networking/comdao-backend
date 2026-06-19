package com.comdao.api.email;

import com.comdao.api.base.BaseExceptionAdvice;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class EmailExceptionAdvice implements BaseExceptionAdvice {
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<Map<String, Object>> handleMessageError(HttpServletRequest request, MessagingException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Email error", null);
    }
}
