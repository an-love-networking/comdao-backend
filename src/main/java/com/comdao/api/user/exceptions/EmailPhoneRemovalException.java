package com.comdao.api.user.exceptions;

import com.comdao.api.base.RFCException;

public class EmailPhoneRemovalException extends RFCException {
    public EmailPhoneRemovalException(String s) {
        super(s, null);
    }
}
