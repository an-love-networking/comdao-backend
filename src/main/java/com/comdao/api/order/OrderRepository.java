package com.comdao.api.order;

import com.comdao.api.order.entities.Order;
import com.comdao.api.order.entities.enums.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomer_Id(Long userId, PageRequest of);

    Optional<Order> findByIdAndCustomer_Id(Long orderId, Long customerId);

    Page<Order> findByCustomer_IdAndState(Long userId, State state, Pageable of);

    Page<Order> findByCustomer_IdAndStateIsNot(Long userId, State state, Pageable of);

    Page<Order> findByCustomer_IdAndStateInOrderByStatePriority(Long userId, Collection<State> states, Pageable of);
}
