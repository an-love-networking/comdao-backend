package com.comdao.api.cart;

import com.comdao.api.base.BaseExceptionAdvice;
import com.comdao.api.cart.exceptions.CartItemNotFoundException;
import com.comdao.api.cart.exceptions.CartItemNotOwnedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CartItemExceptionAdvice implements BaseExceptionAdvice {
    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCartItemNotFound(
            HttpServletRequest request, CartItemNotFoundException e
    ) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Cart Item not exist", null);
    }


    @ExceptionHandler(CartItemNotOwnedException.class)
    public ResponseEntity<Map<String, Object>> handleCartItemNotOwned(
            HttpServletRequest request, CartItemNotOwnedException e
    ) {
        return buildErrorResponse(request, e, HttpStatus.BAD_REQUEST, "Cart Item not owned", null);
    }
}
