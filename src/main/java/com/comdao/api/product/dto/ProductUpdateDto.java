package com.comdao.api.product.dto;

import com.comdao.api.product.entities.enums.Badge;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateDto {
    @NotNull
    @Min(value = 1, message = "Product id cannot be less than 1")
    private Long id;
    @NotBlank
    private String label;
    @NotBlank
    private String description;
    @NotNull
    private Double price;
    //    @NotBlank
//    private String currency;
    private Badge badge;
    @NotBlank
    private String unit;
    @NotNull
    private Boolean retrievable;

    @AssertTrue(message = "Price cannot be less than 0")
    public Boolean isPricePositive() {
        return price > 0.0;
    }
}
