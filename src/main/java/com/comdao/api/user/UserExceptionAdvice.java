package com.comdao.api.user;

import com.comdao.api.base.BaseExceptionAdvice;
import com.comdao.api.user.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class UserExceptionAdvice implements BaseExceptionAdvice {
    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<Map<String, Object>> handleUserDisabled(HttpServletRequest request, UserDisabledException e) {
        return buildErrorResponse(request, e, HttpStatus.CONFLICT, "User Disabled", null);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotExist(HttpServletRequest request, UserNotFoundException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "User Unregistered", null);
    }

    @ExceptionHandler(UpdateInfoCollisionException.class)
    public ResponseEntity<Map<String, Object>> handleUpdateInfoCollide(HttpServletRequest request, UpdateInfoCollisionException e) {
        return buildErrorResponse(request, e, HttpStatus.CONFLICT, "Profile Update Failure", null);
    }

    @ExceptionHandler(EmailPhoneRemovalException.class)
    public ResponseEntity<Map<String, Object>> handleEmailPhoneRemoval(HttpServletRequest request, EmailPhoneRemovalException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Profile Update Failure", null);
    }

    @ExceptionHandler(PasswordPolicyViolationException.class)
    public ResponseEntity<Map<String, Object>> handlePassworldPolicyViolation(HttpServletRequest request, PasswordPolicyViolationException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Password Violation", null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(HttpServletRequest request, BadCredentialsException e) {
        return buildErrorResponse(request, e, HttpStatus.UNAUTHORIZED, "Bad Credentials", Map.of("message", "Wrong password or username"));
    }

    @ExceptionHandler(PasswordUpdateViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIncorrectOldPassword(HttpServletRequest request, BadCredentialsException e) {
        return buildErrorResponse(request, e, HttpStatus.UNAUTHORIZED, "Wrong password", Map.of("message", "Wrong old password"));
    }
}
