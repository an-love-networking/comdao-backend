package com.comdao.api.order.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class OrderNotExistException extends RFCException {
    public OrderNotExistException(String message, Map<String, Object> details) {
        super(message, details);
    }

    public OrderNotExistException(String s) {
        super(s, null);
    }
}
