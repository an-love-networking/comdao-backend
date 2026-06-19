package com.comdao.api.payment;

import com.comdao.api.base.BaseExceptionAdvice;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vn.payos.exception.PayOSException;

import java.util.Map;

public class PayOSExceptionAdvice implements BaseExceptionAdvice {
    @ExceptionHandler(PayOSException.class)
    public ResponseEntity<Map<String, Object>> handlePayOS(HttpServletRequest request,
                                                           PayOSException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "PayOS failure", null);
    }
}
