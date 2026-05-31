package com.comdao.api.category;

import com.comdao.api.category.dto.AllCategoryResponseDto;
import com.comdao.api.category.dto.CategoryCreationDto;
import com.comdao.api.category.dto.CategoryResponseDto;
import com.comdao.api.category.dto.CategoryUpdateDto;
import com.comdao.api.category.entities.Category;
import com.comdao.api.category.exceptions.*;
import com.comdao.api.product.ProductMapper;
import com.comdao.api.product.ProductRepository;
import com.comdao.api.product.entities.Product;
import lombok.RequiredArgsConstructor;
import net.gcardone.junidecode.Junidecode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public AllCategoryResponseDto getAllCategories() {
        AllCategoryResponseDto categories = new AllCategoryResponseDto();
        categories.setCategories(categoryRepository.findByRetrievableTrue());
        return categories;
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategory(Long id) throws
            CategoryNotExistException, CategoryDisabledException {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotExistException("You have not created this category yet")
        );
        if (!category.getRetrievable())
            throw new CategoryDisabledException("This category is disabled");

        CategoryResponseDto response = categoryMapper.toCategoryResponse(category);
        response.setProducts(category.getProducts().stream().map(
                product -> productMapper.convertToProductDto(product)
        ).collect(Collectors.toSet()));
        return response;
    }

    @Transactional(readOnly = true)
    public Category getCategoryAdmin(Long id)
            throws CategoryDisabledException, CategoryNotExistException {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotExistException("You have not created this category yet")
        );
        return category;
    }

    public void checkExists(Set<Product> products) throws CategoryProductNotExistException {
        Set<String> duplicates = new HashSet<>();
        products.forEach(
                product -> {
                    if (!productRepository.existsByIdAndLabel(product.getId(), product.getLabel()))
                        duplicates.add(product.getLabel());
                }
        );

        if (!duplicates.isEmpty())
            throw new CategoryProductNotExistException(
                    "You can not add unexisted products in the category",
                    duplicates
            );
    }

    @Transactional
    public void createCategory(CategoryCreationDto creation)
            throws CategoryCreationViolationException, CategoryProductNotExistException {
        if (categoryRepository.existsByLabel(creation.getLabel()))
            throw new CategoryCreationViolationException("Creating an already existed category");

        checkExists(creation.getProducts());

        Category newCategory = categoryMapper.createCategory(creation);
        newCategory.setNormalizeLabel(Junidecode.unidecode(creation.getLabel()));

        categoryRepository.save(newCategory);
    }

    @Transactional
    public Category updateCategory(CategoryUpdateDto update)
            throws CategoryProductNotExistException, CategoryUpdateViolationException {
        Category category = categoryRepository.findByIdAndLabel(update.getId(), update.getLabel()).orElseThrow(
                () -> new CategoryUpdateViolationException("This category has not been created yet")
        );

        checkExists(update.getProducts());

        categoryMapper.updateCategory(update, category);
        category.setNormalizeLabel(Junidecode.unidecode(update.getLabel()));
        return categoryRepository.save(category);
    }

    @Transactional
    public void disableCategory(Long id) throws CategoryNotExistException {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotExistException("This category has not been created yet"));
        category.setRetrievable(false);
    }

    @Transactional
    public void enableCategory(Long id) throws CategoryNotExistException {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotExistException("This category has not been created yet"));
        category.setRetrievable(true);
    }
}
