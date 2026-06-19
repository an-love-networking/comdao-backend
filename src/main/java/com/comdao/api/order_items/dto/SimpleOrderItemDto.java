package com.comdao.api.order_items.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SimpleOrderItemDto {
    private Long cartItemId;
    private Long productId;
    private Integer quantity;
    private String note;

    @AssertTrue(message = "Must provide order_item_id, or product_id and quantity, but not both")
    public Boolean eitherProductOrOrderItem() {
        return cartItemId != null ^ (productId != null && quantity != null);
    }
}
