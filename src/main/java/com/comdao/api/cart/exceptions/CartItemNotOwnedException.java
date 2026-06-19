package com.comdao.api.cart.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class CartItemNotOwnedException extends RFCException {
    public CartItemNotOwnedException(String message, Map<String, Object> details) {
        super(message, details);
    }

    public CartItemNotOwnedException(String s) {
        super(s, null);
    }
}
