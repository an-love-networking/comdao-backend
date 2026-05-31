package com.comdao.api.category;

import com.comdao.api.category.dto.CategoryIdLabelResponseDto;
import com.comdao.api.category.entities.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<CategoryIdLabelResponseDto> findByRetrievableTrue();

    Boolean existsByLabel(@NotBlank String label);

    Optional<Category> findByIdAndLabel(@NotNull Long id, @NotBlank String label);
}
