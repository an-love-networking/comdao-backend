package com.comdao.api.order;

import com.comdao.api.base.BaseExceptionAdvice;
import com.comdao.api.order.exceptions.OrderAlreadyPaidException;
import com.comdao.api.order.exceptions.OrderCreateViolationException;
import com.comdao.api.order.exceptions.OrderNotExistException;
import com.comdao.api.order.exceptions.OrderNotPaidException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class OrderExceptionAdvice implements BaseExceptionAdvice {
    @ExceptionHandler(OrderAlreadyPaidException.class)
    public ResponseEntity<Map<String, Object>> handleOrderAlreadyPaid(HttpServletRequest request,
                                                                      OrderAlreadyPaidException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Order is already paid", null);
    }

    @ExceptionHandler(OrderCreateViolationException.class)
    public ResponseEntity<Map<String, Object>> handleCreateOrderFailure(HttpServletRequest request,
                                                                        OrderCreateViolationException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Order Creation failure", null);
    }

    @ExceptionHandler(OrderNotExistException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotExist(HttpServletRequest request,
                                                                   OrderNotExistException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Order Not Exist", null);
    }

    @ExceptionHandler(OrderNotPaidException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotPaid(HttpServletRequest request,
                                                                  OrderNotPaidException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Order is not paid", null);
    }
}
