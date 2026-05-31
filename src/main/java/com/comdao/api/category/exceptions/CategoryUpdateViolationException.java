package com.comdao.api.category.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class CategoryUpdateViolationException extends RFCException {
    public CategoryUpdateViolationException(String message, Map<String, String> details) {
        super(message, details);
    }

    public CategoryUpdateViolationException(String s) {
        super(s, null);
    }
}
