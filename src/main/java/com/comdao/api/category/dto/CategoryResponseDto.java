package com.comdao.api.category.dto;

import com.comdao.api.product.dto.ProductDto;
import com.comdao.api.product.entities.enums.Badge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {
    private String label;
    private String description;
    private Badge badge;
    private Set<ProductDto> products;
}
