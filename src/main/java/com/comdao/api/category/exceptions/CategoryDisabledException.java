package com.comdao.api.category.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class CategoryDisabledException extends RFCException {
    public CategoryDisabledException(String message, Map<String, String> details) {
        super(message, details);
    }

    public CategoryDisabledException(String s) {
        super(s, null);
    }
}
