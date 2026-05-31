package com.comdao.api.user.exceptions;

import com.comdao.api.base.RFCException;

public class PasswordPolicyViolationException extends RFCException {
    public PasswordPolicyViolationException(String s) {
        super(s, null);
    }
}
