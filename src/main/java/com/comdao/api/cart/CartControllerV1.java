package com.comdao.api.cart;

import com.comdao.api.cart.dto.CartCreationRequestDto;
import com.comdao.api.cart.dto.CartItemResponseDto;
import com.comdao.api.cart.dto.CartItemUpdateRequestDto;
import com.comdao.api.cart.exceptions.CartItemNotFoundException;
import com.comdao.api.cart.exceptions.CartItemNotOwnedException;
import com.comdao.api.product.exceptions.ProductNotExistException;
import com.comdao.api.user.exceptions.UserDisabledException;
import com.comdao.api.user.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@Slf4j
public class CartControllerV1 {
    private final CartItemService cartItemService;


    @GetMapping("view")
    public ResponseEntity<Page<CartItemResponseDto>> getCart(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) throws UserNotFoundException, UserDisabledException {
        log.info("Viewing cart from user #{} on page {} size {}", userId, page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartItemService.getCart(userId, page, size));
    }


    @PostMapping("add")
    public ResponseEntity<Void> addToCart(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CartCreationRequestDto newCartItem
    ) throws UserDisabledException, UserNotFoundException, ProductNotExistException {
        log.info("User {} add to cart item {}", userId, newCartItem);
        cartItemService.addToCart(userId, newCartItem);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PutMapping("update")
    public ResponseEntity<CartItemResponseDto> updateCartItem(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CartItemUpdateRequestDto updatedCartItem
    ) throws UserNotFoundException, UserDisabledException,
            CartItemNotFoundException, CartItemNotOwnedException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartItemService.updateCartItem(userId, updatedCartItem));
    }


    @DeleteMapping("delete")
    public ResponseEntity<Void> removeFromCart(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "id") Long cartId
    ) throws UserDisabledException, UserNotFoundException,
            CartItemNotFoundException, CartItemNotOwnedException {
        cartItemService.removeFromCart(userId, cartId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
