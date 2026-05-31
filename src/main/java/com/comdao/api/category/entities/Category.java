package com.comdao.api.category.entities;

import com.comdao.api.product.entities.Product;
import com.comdao.api.product.entities.enums.Badge;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Category {
    @Id
    @GeneratedValue
    private Long id;
    @ToString.Include
    private String label;
    @ToString.Include
    private String description;

    @Enumerated(value = EnumType.STRING)
    private Badge badge;
    private String normalizeLabel;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate created = LocalDate.now();
    private Boolean retrievable = true;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "category_products",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new HashSet<>();

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
    }
}
