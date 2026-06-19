package com.comdao.api.category.dto;

import com.comdao.api.product.entities.enums.Badge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateDto {
    @NotNull
    private Long id;
    @NotBlank
    private String label;
    @NotBlank
    private String description;
    private Badge badge;
    @Size(min = 1, message = "Category must have at least 1 product")
    private Set<Long> productIds;
    @NotNull
    private Boolean retrievable;
}
