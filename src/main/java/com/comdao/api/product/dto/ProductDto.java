package com.comdao.api.product.dto;

import com.comdao.api.product.entities.enums.Badge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String label;
    private String description;
    private Double price;
    private String currency;
    private Badge badge;
    private String unit;
    private String imageUrl;
}
