package com.comdao.api.category;

import com.comdao.api.base.BaseExceptionAdvice;
import com.comdao.api.category.exceptions.CategoryCreationViolationException;
import com.comdao.api.category.exceptions.CategoryDisabledException;
import com.comdao.api.category.exceptions.CategoryNotExistException;
import com.comdao.api.category.exceptions.CategoryUpdateViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CategoryExceptionAdvice implements BaseExceptionAdvice {
    @ExceptionHandler(CategoryCreationViolationException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryCreationGoneWrong(
            HttpServletRequest request,
            CategoryCreationViolationException e
    ) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Creation failure", null);
    }

    @ExceptionHandler(CategoryDisabledException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryCreationGoneWrong(
            HttpServletRequest request,
            CategoryDisabledException e
    ) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Category disabled", null);
    }

    @ExceptionHandler(CategoryNotExistException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryNotExistExceptionGoneWrong(
            HttpServletRequest request,
            CategoryNotExistException e
    ) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Category non exist", null);
    }

    @ExceptionHandler(CategoryUpdateViolationException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryUpdateViolationExceptionGoneWrong(
            HttpServletRequest request,
            CategoryUpdateViolationException e
    ) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Update failure", null);
    }
}
