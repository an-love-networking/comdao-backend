package com.comdao.api.product.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class ProductNotRetrievableException extends RFCException {
    public ProductNotRetrievableException(String message, Map<String, Object> details) {
        super(message, details);
    }

    public ProductNotRetrievableException(String s) {
        super(s, null);
    }
}
