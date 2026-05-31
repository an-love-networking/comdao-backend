package com.comdao.api.product;

import com.comdao.api.product.dto.ProductCreationDto;
import com.comdao.api.product.dto.ProductDto;
import com.comdao.api.product.dto.ProductUpdateDto;
import com.comdao.api.product.entities.Product;
import com.comdao.api.product.exceptions.ProductDuplicationCreationException;
import com.comdao.api.product.exceptions.ProductNotExistException;
import lombok.RequiredArgsConstructor;
import net.gcardone.junidecode.Junidecode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsFiltered(String filter, Integer size, Integer page) {
        return productRepository.findFilteredProduct(filter, PageRequest.of(page, size))
                .map(prod -> mapper.convertToProductDto(prod));
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsFilteredAdmin(String filter, Integer size, Integer page) {
        return productRepository.findFilteredProductAdmin(filter, PageRequest.of(page, size));
    }

    @Transactional
    public void createProduct(ProductCreationDto newProduct)
            throws ProductDuplicationCreationException {
        if (productRepository.existsByLabel(newProduct.getLabel()))
            throw new ProductDuplicationCreationException("Cannot create an existed product");
        Product product = mapper.convertToProduct(newProduct);
        product.setNormalizeLabel(Junidecode.unidecode(product.getLabel()));
        productRepository.save(product);
    }

    @Transactional
    public ProductDto updateProduct(ProductUpdateDto update) throws ProductNotExistException {
        Product product = productRepository.findById(update.getId()).orElseThrow(
                () -> new ProductNotExistException("Product doesn't exist"));

        mapper.updateProduct(update, product);
        product.setNormalizeLabel(Junidecode.unidecode(update.getLabel()));
        productRepository.save(product);
        return mapper.convertToProductDto(product);
    }

    @Transactional
    public void disableProduct(Long id)
            throws ProductNotExistException {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotExistException("Product doesn't exist"));

        product.setRetrievable(false);
        productRepository.save(product);
    }

    @Transactional
    public void enableProduct(Long id)
            throws ProductNotExistException {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotExistException("Product doesn't exist"));

        product.setRetrievable(true);
        productRepository.save(product);
    }
}
