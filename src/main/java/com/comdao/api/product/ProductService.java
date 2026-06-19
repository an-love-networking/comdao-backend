package com.comdao.api.product;

import com.comdao.api.product.dto.ProductCreationDto;
import com.comdao.api.product.dto.ProductDto;
import com.comdao.api.product.dto.ProductUpdateDto;
import com.comdao.api.product.entities.Product;
import com.comdao.api.product.exceptions.ProductDuplicationCreationException;
import com.comdao.api.product.exceptions.ProductNotExistException;
import com.comdao.api.s3.ImageUploadService;
import lombok.RequiredArgsConstructor;
import net.gcardone.junidecode.Junidecode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    private final ImageUploadService imageUploadService;


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
    public Product createProduct(ProductCreationDto newProduct, MultipartFile file)
            throws ProductDuplicationCreationException, IOException {
        if (productRepository.existsByLabel(newProduct.getLabel()))
            throw new ProductDuplicationCreationException("Cannot create an existed product");
        String uuid = imageUploadService.uploadImage(file, "products");

        Product product = mapper.convertToProduct(newProduct);
        product.setNormalizeLabel(Junidecode.unidecode(product.getLabel()));
        product.setImageUrl(uuid);

        return productRepository.save(product);
    }


    @Transactional
    public Product updateProduct(ProductUpdateDto updatedProduct, MultipartFile file)
            throws ProductNotExistException, IOException {
        Product product = productRepository.findById(updatedProduct.getId()).orElseThrow(
                () -> new ProductNotExistException("Product doesn't exist"));

        String uuid = imageUploadService.uploadImage(file, "products");
        imageUploadService.delete(product.getImageUrl());

        mapper.updateProduct(updatedProduct, product);
        product.setNormalizeLabel(Junidecode.unidecode(updatedProduct.getLabel()));
        productRepository.save(product);
        return product;
    }
//
//    @Transactional
//    public void disableProduct(Long productId)
//            throws ProductNotExistException {
//        Product product = productRepository.findById(productId).orElseThrow(
//                () -> new ProductNotExistException("Product doesn't exist"));
//
//        product.setRetrievable(false);
//        productRepository.save(product);
//    }
//
//    @Transactional
//    public void enableProduct(Long productId)
//            throws ProductNotExistException {
//        Product product = productRepository.findById(productId).orElseThrow(
//                () -> new ProductNotExistException("Product doesn't exist"));
//
//        product.setRetrievable(true);
//        productRepository.save(product);
//    }
}
