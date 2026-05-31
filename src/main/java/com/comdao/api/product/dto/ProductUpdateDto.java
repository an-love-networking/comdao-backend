package com.comdao.api.product.dto;

import com.comdao.api.product.entities.enums.Badge;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateDto {
    @NonNull
    @Min(value = 1, message = "You cannot update product that has non-positive id")
    private Long id;
    @NotBlank
    private String label;
    @NotBlank
    private String description;
    @NonNull
    @Min(value = 0, message = "Price cannot be less than 0")
    private Double price;
    @NotBlank
    private String currency;
    private Badge badge;
    @NotBlank
    private String unit;
}
