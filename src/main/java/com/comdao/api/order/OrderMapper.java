package com.comdao.api.order;

import com.comdao.api.order.dto.OrderResponseDto;
import com.comdao.api.order.dto.ShortOrderResponseDto;
import com.comdao.api.order.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    public OrderResponseDto toOrderResponse(Order order);

    public ShortOrderResponseDto toShortOrderResponse(Order order);
}
