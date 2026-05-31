package com.comdao.api.base;

import lombok.Getter;

import java.util.Map;

@Getter
public class RFCException extends Exception {
    Object details;

    public RFCException(String message, Map<String, String> details) {
        super(message);
        this.details = details;
    }
}
