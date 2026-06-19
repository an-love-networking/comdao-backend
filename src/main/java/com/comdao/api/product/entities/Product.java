package com.comdao.api.product.entities;

import com.comdao.api.product.entities.enums.Badge;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * <p>Entity for Product</p>
 *
 * @author an-love-networking
 * @since 1.0
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq_gen")
    @SequenceGenerator(name = "product_seq_gen", allocationSize = 1, sequenceName = "product_seq")
    private Long id;
    @ToString.Include
    @EqualsAndHashCode.Include
    private String label;
    @ToString.Include
    private String description;
    private Double price;
    private String currency = "VND";
    private String unit;

    @Enumerated(value = EnumType.STRING)
    private Badge badge = null;
    @JsonIgnore
    private String normalizeLabel;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate created = LocalDate.now();
    private Boolean retrievable = true;

    private String imageUrl;

    @Version
    @JsonIgnore
    private Long version;
}
