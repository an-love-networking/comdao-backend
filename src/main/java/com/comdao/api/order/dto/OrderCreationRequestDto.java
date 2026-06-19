package com.comdao.api.order.dto;

import com.comdao.api.order_items.dto.SimpleOrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderCreationRequestDto {
    private Set<SimpleOrderItemDto> orderItems = new HashSet<>();
    private Double discount;
}
