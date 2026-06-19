package com.comdao.api.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInfo {
    private Boolean isEnough;
    private Double unpaidAmount;
}
