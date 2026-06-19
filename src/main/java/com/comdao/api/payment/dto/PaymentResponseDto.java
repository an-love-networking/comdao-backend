package com.comdao.api.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private String qrCode;
//    private Long amount;
//    private String currency;
//    private String description;
}
