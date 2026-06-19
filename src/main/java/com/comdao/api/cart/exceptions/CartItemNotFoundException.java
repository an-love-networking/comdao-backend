package com.comdao.api.cart.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class CartItemNotFoundException extends RFCException {
    public CartItemNotFoundException(String message, Map<String, Object> details) {
        super(message, details);
    }

    public CartItemNotFoundException(String s) {
        super(s, null);
    }
}
