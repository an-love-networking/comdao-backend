package com.comdao.api.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreationInfoDto {
    @NotNull
    private Long orderId;
    private String description;
}
