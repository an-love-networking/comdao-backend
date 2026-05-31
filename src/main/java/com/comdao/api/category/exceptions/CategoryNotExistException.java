package com.comdao.api.category.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class CategoryNotExistException extends RFCException {
    public CategoryNotExistException(String message, Map<String, String> details) {
        super(message, details);
    }

    public CategoryNotExistException(String s) {
        super(s, null);
    }
}
