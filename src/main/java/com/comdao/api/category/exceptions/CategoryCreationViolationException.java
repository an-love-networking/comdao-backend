package com.comdao.api.category.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class CategoryCreationViolationException extends RFCException {
    public CategoryCreationViolationException(String message, Map<String, Object> details) {
        super(message, details);
    }

    public CategoryCreationViolationException(String s) {
        super(s, null);
    }
}
