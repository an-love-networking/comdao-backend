package com.comdao.api.product.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class ProductNotExistException extends RFCException {
    public ProductNotExistException(String s) {
        super(s, null);
    }

    public ProductNotExistException(String message, Map<String, String> details) {
        super(message, details);
    }
}
