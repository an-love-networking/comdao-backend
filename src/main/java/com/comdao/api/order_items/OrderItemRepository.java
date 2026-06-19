package com.comdao.api.order_items;

import com.comdao.api.order_items.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
//    Page<OrderItem> findByUser_IdAndOrderIsNull(Long id, PageRequest of);

//    Optional<OrderItem> findByIdAndUser_Id(Long orderItemId, Long userId);
}
