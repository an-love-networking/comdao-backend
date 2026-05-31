package com.comdao.api.product.entities;

import com.comdao.api.product.entities.enums.Badge;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    @ToString.Include
    @EqualsAndHashCode.Include
    private String label;
    @ToString.Include
    private String description;
    private Double price;
    private String currency;
    private String unit;

    @Enumerated(value = EnumType.STRING)
    private Badge badge = null;
    private String normalizeLabel;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate created = LocalDate.now();
    private Boolean retrievable = true;
}
