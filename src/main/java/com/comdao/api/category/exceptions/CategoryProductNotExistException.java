package com.comdao.api.category.exceptions;

import com.comdao.api.base.RFCException;

import java.util.Set;

public class CategoryProductNotExistException extends RFCException {
    Set<String> duplicates;

    public CategoryProductNotExistException(String s, Set<String> details) {
        super(s, null);
        this.duplicates = details;
    }

    public Object getDuplicates() {
        return duplicates;
    }
}
