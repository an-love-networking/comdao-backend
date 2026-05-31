package com.comdao.api.user.exceptions;

import com.comdao.api.base.RFCException;

import java.util.HashMap;

public class UpdateInfoCollisionException extends RFCException {
    public UpdateInfoCollisionException(String s, HashMap<String, String> details) {
        super(s, details);
    }
}
