package com.comdao.api.payment.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class PaymentViolationException extends RFCException {
    public PaymentViolationException(String message, Map<String, Object> details) {
        super(message, details);
    }

    public PaymentViolationException(String s) {
        super(s, null);
    }
}
