package com.comdao.api.user.exceptions;

import com.comdao.api.base.RFCException;

public class UserDisabledException extends RFCException {
    public UserDisabledException(String s) {
        super(s, null);
    }
}
