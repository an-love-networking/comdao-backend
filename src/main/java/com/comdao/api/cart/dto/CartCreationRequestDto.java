package com.comdao.api.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartCreationRequestDto {
    private Long productId;
    private Integer quantity;
}
