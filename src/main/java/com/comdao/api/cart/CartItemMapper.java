package com.comdao.api.cart;

import com.comdao.api.cart.dto.CartCreationRequestDto;
import com.comdao.api.cart.dto.CartItemResponseDto;
import com.comdao.api.cart.entities.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItemResponseDto toResponse(CartItem cartItem);

    CartItem createCartItem(CartCreationRequestDto newCartItem);
}
