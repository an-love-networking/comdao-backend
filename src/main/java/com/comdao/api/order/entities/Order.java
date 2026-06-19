package com.comdao.api.order.entities;

import com.comdao.api.order.entities.enums.State;
import com.comdao.api.order_items.entities.OrderItem;
import com.comdao.api.user.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"customer"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_cart_gen")
    @SequenceGenerator(name = "order_seq_gen", sequenceName = "order_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @EqualsAndHashCode.Include
    private User customer;
    @Enumerated(value = EnumType.STRING)
    private State state;
    @JsonIgnore
    private int statePriority;

    @EqualsAndHashCode.Include
    private LocalDateTime created;
    @EqualsAndHashCode.Include
    private LocalDateTime cancelled;
    @EqualsAndHashCode.Include
    private LocalDateTime finished;

    @OneToMany(mappedBy = "order")
    private Set<OrderItem> orderItems = new HashSet<>();

    private Double subtotal;
    private Double discount;
    private Double total;
    private String currency;

    //    private PaymentInfo paymentInfo;
    private String paymentQrCode;

    private Double paidAmount = 0.0;
    private Double changeAmount = 0.0;
    @Version
    private Long version;

    public void addPaidAmount(Double paidAmount) {
        this.paidAmount += paidAmount;
    }

    public void deductPaidAmount(Double amount) {
        this.paidAmount -= amount;
    }

    public void setState(State state) {
        this.state = state;
        this.statePriority = state.getStatusPriority();
    }
}
