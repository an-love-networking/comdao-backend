package com.comdao.api.notices;

import com.comdao.api.base.BaseExceptionAdvice;
import com.comdao.api.notices.exceptions.NoticeCreationViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class NoticeExceptionAdvice implements BaseExceptionAdvice {
    @ExceptionHandler(NoticeCreationViolationException.class)
    public ResponseEntity<Map<String, Object>> handleNoticeCreationFailure(HttpServletRequest request,
                                                                           NoticeCreationViolationException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Notice Creation failure", null);
    }
}
