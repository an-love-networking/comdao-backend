package com.comdao.api.order.dto;

import com.comdao.api.order.entities.enums.State;
import com.comdao.api.order_items.entities.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long id;
    private State state;

    private LocalDateTime created;
    private LocalDateTime cancelled;
    private LocalDateTime finished;

    private Set<OrderItem> orderItems = new HashSet<>();

    private Double subtotal;
    private Double discount;
    private Double total;
    private String currency;
}
