package com.comdao.api.cart;

import com.comdao.api.cart.entities.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByProductIdAndUser_Id(Long productId, Long userId);

    Page<CartItem> findByUser_Id(Long userId, Pageable of);
}
