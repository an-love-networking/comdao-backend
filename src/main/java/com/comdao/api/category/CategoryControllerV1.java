package com.comdao.api.category;

import com.comdao.api.category.dto.AllCategoryResponseDto;
import com.comdao.api.category.dto.CategoryCreationDto;
import com.comdao.api.category.dto.CategoryResponseDto;
import com.comdao.api.category.dto.CategoryUpdateDto;
import com.comdao.api.category.entities.Category;
import com.comdao.api.category.exceptions.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryControllerV1 {
    private final CategoryService categoryService;

    @GetMapping("all")
    public ResponseEntity<AllCategoryResponseDto> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.getAllCategories());
    }

    @GetMapping("view")
    public ResponseEntity<CategoryResponseDto> getCategory(@RequestParam(name = "id") Long id)
            throws CategoryDisabledException, CategoryNotExistException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.getCategory(id));
    }

    @GetMapping("view/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> getCategoryAdmin(@RequestParam(name = "id") Long id)
            throws CategoryDisabledException, CategoryNotExistException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.getCategoryAdmin(id));
    }

    @PostMapping("create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createCategory(@Valid @RequestBody CategoryCreationDto creation)
            throws CategoryCreationViolationException, CategoryProductNotExistException {
        categoryService.createCategory(creation);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("update")
    public ResponseEntity<Category> updateCategory(@Valid @RequestBody CategoryUpdateDto update)
            throws CategoryProductNotExistException, CategoryUpdateViolationException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.updateCategory(update));
    }

    @DeleteMapping("disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableCategory(@RequestParam(name = "id") Long id)
            throws CategoryNotExistException {
        categoryService.disableCategory(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enableCategory(@RequestParam(name = "id") Long id)
            throws CategoryNotExistException {
        categoryService.enableCategory(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
