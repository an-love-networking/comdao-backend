package com.comdao.api.product.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class ProductDuplicationCreationException extends RFCException {
    public ProductDuplicationCreationException(String s) {
        super(s, null);
    }

    public ProductDuplicationCreationException(String message, Map<String, Object> details) {
        super(message, details);
    }
}
