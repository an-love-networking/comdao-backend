package com.comdao.api.product.dto;

import com.comdao.api.product.entities.enums.Badge;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreationDto {
    @NotBlank
    private String label;
    @NotBlank
    private String description;
    @NotNull
    private Double price;
    //    @NotBlank
//    private String currency;
    @NotBlank
    private String unit;
    private Badge badge;

    @AssertTrue(message = "Price cannot be less than 0")
    public Boolean isPricePositive() {
        return price > 0.0;
    }
}
