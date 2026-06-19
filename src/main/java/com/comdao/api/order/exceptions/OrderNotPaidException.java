package com.comdao.api.order.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class OrderNotPaidException extends RFCException {
    public OrderNotPaidException(String s) {
        super(s, null);
    }

    public OrderNotPaidException(String message, Map<String, Object> details) {
        super(message, details);
    }
}
