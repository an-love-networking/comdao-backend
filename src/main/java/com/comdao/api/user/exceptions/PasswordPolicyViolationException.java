package com.comdao.api.user.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class PasswordPolicyViolationException extends RFCException {
    public PasswordPolicyViolationException(String s) {
        super(s, null);
    }

    public PasswordPolicyViolationException(String message, Map<String, Object> details) {
        super(message, details);
    }
}
