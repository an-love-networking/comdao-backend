package com.comdao.api.base;

import lombok.Getter;

import java.util.Map;

@Getter
public class RFCException extends Exception {
    Map<String, Object> details;

    public RFCException(String message, Map<String, Object> details) {
        super(message);
        this.details = details;
    }
}
