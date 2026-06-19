package com.comdao.api.cart.entities;

import com.comdao.api.product.entities.Product;
import com.comdao.api.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "cart_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_item_cart_gen")
    @SequenceGenerator(name = "cart_item_seq_gen", sequenceName = "cart_item_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
