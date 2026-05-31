package com.comdao.api.user.exceptions;

import com.comdao.api.base.RFCException;

public class UserNotExistException extends RFCException {
    public UserNotExistException(String s) {
        super(s, null);
    }
}
