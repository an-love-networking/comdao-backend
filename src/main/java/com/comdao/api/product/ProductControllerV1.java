package com.comdao.api.product;

import com.comdao.api.product.dto.ProductCreationDto;
import com.comdao.api.product.dto.ProductDto;
import com.comdao.api.product.dto.ProductUpdateDto;
import com.comdao.api.product.entities.Product;
import com.comdao.api.product.exceptions.ProductDuplicationCreationException;
import com.comdao.api.product.exceptions.ProductNotExistException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductControllerV1 {
    private final ProductService productService;

    @GetMapping("view")
    public ResponseEntity<Page<ProductDto>> getProduct(
            @RequestParam(name = "filter_content", required = false) String filterContent,
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "page") Integer page) {
        if (filterContent == null) filterContent = "";
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.getProductsFiltered(filterContent, size, page));
    }

    @GetMapping("view/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Product>> getProductAdmin(
            @RequestParam(name = "filter_content", required = false) String filterContent,
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "page") Integer page) {
        if (filterContent == null) filterContent = "";
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.getProductsFilteredAdmin(filterContent, size, page));
    }


    @PostMapping("create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createProduct(@Valid @RequestBody ProductCreationDto newProduct)
            throws ProductDuplicationCreationException {
        productService.createProduct(newProduct);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductUpdateDto update)
            throws ProductNotExistException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.updateProduct(update));
    }

    @DeleteMapping("disable")
    public ResponseEntity<Void> disableProduct(@RequestParam(name = "id") Long id)
            throws ProductNotExistException {
        productService.disableProduct(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("enable")
    public ResponseEntity<Void> enableProduct(@RequestParam(name = "id") Long id)
            throws ProductNotExistException {
        productService.enableProduct(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
