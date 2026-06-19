package com.comdao.api.base;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public interface BaseExceptionAdvice {
    default Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    default ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpServletRequest request,
            Exception e,
            HttpStatus status,
            String error,
            Object details
    ) {
        log().error(e.getMessage());
        log().error(error);

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", e.getMessage());

        if (details != null) body.put("details", details);

        if (e instanceof RFCException) {
            if (((RFCException) e).getDetails() != null)
                body.put("details", ((RFCException) e).getDetails());
        }

//        body.put("traceId", request.getHeader("X-Correlation-Id"));
        body.put("path", request.getRequestURI());

        return new ResponseEntity<Map<String, Object>>(body, status);
    }
}
