package com.comdao.api.product;

import com.comdao.api.product.dto.ProductCreationDto;
import com.comdao.api.product.dto.ProductDto;
import com.comdao.api.product.dto.ProductUpdateDto;
import com.comdao.api.product.entities.Product;
import com.comdao.api.product.exceptions.ProductDuplicationCreationException;
import com.comdao.api.product.exceptions.ProductNotExistException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
//@CrossOrigin(origins = "*")
public class ProductControllerV1 {
    private final ProductService productService;


    @GetMapping("view")
    public ResponseEntity<Page<ProductDto>> getProduct(
            @RequestParam(name = "filter_content", required = false, defaultValue = "") String filterContent,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {
        log.info("Enter fetch product");
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.getProductsFiltered(filterContent, size, page));
    }


    @GetMapping("view/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Product>> getProductAdmin(
            @RequestParam(name = "filter_content", required = false, defaultValue = "") String filterContent,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {
        log.info("Enter admin fetch product");
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.getProductsFilteredAdmin(filterContent, size, page));
    }


    @PostMapping(value = "create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createProduct(
            @Valid @RequestPart("data") ProductCreationDto newProduct,
            @RequestPart("file") MultipartFile file
    ) throws ProductDuplicationCreationException, IOException {
        log.info("Entered image upload controller");
        productService.createProduct(newProduct, file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PutMapping(value = "update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(
            @Valid @RequestPart("data") ProductUpdateDto update,
            @RequestPart("file") MultipartFile file
    ) throws ProductNotExistException, IOException {
        log.info("Enter update product");
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.updateProduct(update, file));
    }
//
//
//    @DeleteMapping("disable")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> disableProduct(@RequestParam(name = "id") Long id)
//            throws ProductNotExistException {
//        productService.disableProduct(id);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
//
//
//    @PatchMapping("enable")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> enableProduct(@RequestParam(name = "id") Long id)
//            throws ProductNotExistException {
//        productService.enableProduct(id);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
}
