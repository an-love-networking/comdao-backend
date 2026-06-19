package com.comdao.api.product;

import com.comdao.api.product.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.normalizeLabel ILIKE CONCAT('%', :label , '%') AND p.retrievable = true")
    public Page<Product> findFilteredProduct(String label, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.normalizeLabel ILIKE CONCAT('%', :label , '%')")
    public Page<Product> findFilteredProductAdmin(String label, Pageable pageable);

    public Page<Product> findByRetrievableTrue(Pageable pageable);

    Boolean existsByLabel(String label);

    Boolean existsByIdAndLabel(Long id, String label);

    Boolean existsByIdAndRetrievableTrue(Long productId);

    Optional<Product> findByLabel(String label);
}
