package com.comdao.api.notices.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Map;

public class NoticeCreationViolationException extends RFCException {
    public NoticeCreationViolationException(String message, Map<String, Object> details) {
        super(message, details);
    }

    public NoticeCreationViolationException(String s) {
        super(s, null);
    }
}
