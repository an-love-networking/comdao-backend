package com.comdao.api.cart.dto;

import com.comdao.api.product.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {
    private Long id;
    private Product product;
    private Integer quantity;
}
