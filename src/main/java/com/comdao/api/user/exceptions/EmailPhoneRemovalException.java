package com.comdao.api.user.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class EmailPhoneRemovalException extends RFCException {
    public EmailPhoneRemovalException(String s) {
        super(s, null);
    }

    public EmailPhoneRemovalException(String message, Map<String, Object> details) {
        super(message, details);
    }
}
