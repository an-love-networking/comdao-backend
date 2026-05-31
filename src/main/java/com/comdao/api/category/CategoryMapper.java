package com.comdao.api.category;

import com.comdao.api.category.dto.CategoryCreationDto;
import com.comdao.api.category.dto.CategoryResponseDto;
import com.comdao.api.category.dto.CategoryUpdateDto;
import com.comdao.api.category.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "products", ignore = true)
    CategoryResponseDto toCategoryResponse(Category category);

    Category createCategory(CategoryCreationDto creation);

    void updateCategory(CategoryUpdateDto update, @MappingTarget Category category);
}
