package com.comdao.api.order_items.dto;

import com.comdao.api.product.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {
    private Product product;
    private Integer quantity;
}
