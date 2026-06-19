package com.comdao.api.order.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class OrderCreateViolationException extends RFCException {
    public OrderCreateViolationException(String message, Map<String, Object> details) {
        super(message, details);
    }

    public OrderCreateViolationException(String s) {
        super(s, null);
    }
}
