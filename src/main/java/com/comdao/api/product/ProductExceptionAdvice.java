package com.comdao.api.product;

import com.comdao.api.base.BaseExceptionAdvice;
import com.comdao.api.product.exceptions.ProductDuplicationCreationException;
import com.comdao.api.product.exceptions.ProductNotExistException;
import com.comdao.api.product.exceptions.ProductNotRetrievableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ProductExceptionAdvice implements BaseExceptionAdvice {
    @ExceptionHandler(ProductDuplicationCreationException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateCreation(HttpServletRequest request,
                                                                       ProductDuplicationCreationException e) {
        return buildErrorResponse(request, e, HttpStatus.CONFLICT, "Duplicated Item", null);
    }

    @ExceptionHandler(ProductNotExistException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotExist(
            HttpServletRequest request, ProductNotExistException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Product not found", null);
    }

    @ExceptionHandler(ProductNotRetrievableException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotRetrievable(HttpServletRequest request,
                                                                           ProductNotRetrievableException e) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Product not available at the moment", null);
    }
}
