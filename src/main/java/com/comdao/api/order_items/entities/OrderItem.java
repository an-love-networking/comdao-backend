package com.comdao.api.order_items.entities;


import com.comdao.api.order.entities.Order;
import com.comdao.api.product.entities.Product;
import com.comdao.api.user.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>Entity for OrderItem</p>
 *
 * @author an-love-networking
 * @see User
 * @see Order
 * @see Product
 * @since 1.0
 */
@Entity
@Table(name = "order_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"order"})
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_cart_gen")
    @SequenceGenerator(name = "order_item_seq_gen", sequenceName = "order_item_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer quantity;
    private String note;

    public void setProduct(Product product) {
        if (this.product != null)
            throw new IllegalStateException("OrderItem's product can only be set once");
        this.product = product;
    }

    public void setOrder(Order order) {
        if (this.order != null)
            throw new IllegalStateException("OrderItem's order can only be set once");
        this.order = order;
    }
}
