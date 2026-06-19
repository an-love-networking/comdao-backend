package com.comdao.api.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemUpdateRequestDto {
    private Long id;
    private Integer quantity;
}
