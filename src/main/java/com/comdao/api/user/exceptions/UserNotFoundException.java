package com.comdao.api.user.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class UserNotFoundException extends RFCException {
    public UserNotFoundException(String s) {
        super(s, null);
    }

    public UserNotFoundException(String message, Map<String, Object> details) {
        super(message, details);
    }
}
