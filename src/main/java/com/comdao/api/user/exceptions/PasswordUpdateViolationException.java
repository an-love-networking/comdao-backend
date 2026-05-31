package com.comdao.api.user.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class PasswordUpdateViolationException extends RFCException {
    public PasswordUpdateViolationException(String message, Map<String, String> details) {
        super(message, details);
    }

    public PasswordUpdateViolationException(String s) {
        super(s, null);
    }
}
