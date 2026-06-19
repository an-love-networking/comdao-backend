package com.comdao.api.order.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class OrderAlreadyPaidException extends RFCException {
    public OrderAlreadyPaidException(String message, Map<String, Object> details) {
        super(message, details);
    }

    public OrderAlreadyPaidException(String s) {
        super(s, null);
    }
}
