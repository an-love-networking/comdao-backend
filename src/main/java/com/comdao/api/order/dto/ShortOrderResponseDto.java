package com.comdao.api.order.dto;

import com.comdao.api.order.entities.enums.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortOrderResponseDto {
    private Long id;
    private State state;
    private Double total;
    private String currency;

    private LocalDateTime created;
    private LocalDateTime cancelled;
    private LocalDateTime finished;
}
