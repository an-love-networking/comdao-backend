package com.comdao.api.category;

import com.comdao.api.category.dto.CategoryCreationDto;
import com.comdao.api.category.dto.CategoryIdLabelResponseDto;
import com.comdao.api.category.dto.CategoryResponseDto;
import com.comdao.api.category.dto.CategoryUpdateDto;
import com.comdao.api.category.entities.Category;
import com.comdao.api.category.exceptions.CategoryCreationViolationException;
import com.comdao.api.category.exceptions.CategoryDisabledException;
import com.comdao.api.category.exceptions.CategoryNotExistException;
import com.comdao.api.category.exceptions.CategoryUpdateViolationException;
import com.comdao.api.product.ProductMapper;
import com.comdao.api.product.ProductRepository;
import com.comdao.api.product.dto.ProductDto;
import com.comdao.api.product.entities.Product;
import com.comdao.api.product.exceptions.ProductNotExistException;
import lombok.RequiredArgsConstructor;
import net.gcardone.junidecode.Junidecode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public List<CategoryIdLabelResponseDto> getAllCategories() {
//        AllCategoryResponseDto categories = new AllCategoryResponseDto();
//        categories.setCategories(categoryRepository.findByRetrievableTrue());
        return categoryRepository.findByRetrievableTrue();
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
        Set<ProductDto> products = new HashSet<>();
        for (Product product : category.getProducts()) {
            if (product.getRetrievable())
                products.add(productMapper.convertToProductDto(product));
        }
        response.setProducts(products);

        return response;
    }


    @Transactional(readOnly = true)
    public Category getCategoryAdmin(Long id)
            throws CategoryNotExistException {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotExistException("You have not created this category yet")
        );
        return category;
    }


    @Transactional(readOnly = true)
    public void checkExistBeforeCreate(Set<Long> productIds)
            throws ProductNotExistException {
        productIds = new HashSet<>(productIds);

        List<Long> nonExistProduct = productIds.stream().filter(
                productId -> !productRepository.existsById(productId)
        ).toList();

        System.out.println(productIds);
        System.out.println(nonExistProduct);
        System.out.println("-----------------------");
        if (!nonExistProduct.isEmpty())
            throw new ProductNotExistException("Product does not exist", Map.of("non_exists", nonExistProduct));
    }


    @Transactional
    public Category createCategory(CategoryCreationDto creation)
            throws CategoryCreationViolationException, ProductNotExistException {
        if (categoryRepository.existsByLabel(creation.getLabel()))
            throw new CategoryCreationViolationException("Creating an already existed category");

        checkExistBeforeCreate(creation.getProductIds());
        System.out.println(creation.getProductIds());

        Set<Product> products = creation.getProductIds().stream().map(
                product -> productRepository.findById(product).get()
        ).collect(Collectors.toSet());

        Category newCategory = categoryMapper.createCategory(creation);
        newCategory.setProducts(products);
        newCategory.setNormalizeLabel(Junidecode.unidecode(creation.getLabel()));

        return categoryRepository.save(newCategory);
    }


    @Transactional
    public Category updateCategory(CategoryUpdateDto update)
            throws CategoryUpdateViolationException, ProductNotExistException {
        Category category = categoryRepository.findByIdAndLabel(update.getId(), update.getLabel()).orElseThrow(
                () -> new CategoryUpdateViolationException("This category has not been created yet")
        );

        checkExistBeforeCreate(update.getProductIds());
        Set<Product> products = update.getProductIds().stream().map(
                product -> productRepository.findById(product).get()
        ).collect(Collectors.toSet());

        categoryMapper.updateCategory(update, category);
        category.setProducts(products);
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
