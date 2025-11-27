package com.example.Event_Manager.category.controller;

import com.example.Event_Manager._util.annotations.IsAdmin;
import com.example.Event_Manager.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import com.example.Event_Manager.category.service.ICategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin
@Validated
public class CategoryController implements CategoryApi {

    private final ICategoryService categoryService;

    @PostMapping
    @IsAdmin
    public ResponseEntity<CategoryDTO> createCategory(
            @Valid @RequestBody CreateCategoryDTO createCategoryDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(createCategoryDTO));
    }

    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable("id") @Positive(message = "Id should be positive") Long id,
            @Valid @RequestBody UpdateCategoryDTO updateCategoryDTO
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, updateCategoryDTO));
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> deleteCategory(
            @PathVariable("id") @Positive(message = "Id should be positive") Long id
    ) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @PathVariable("id") @Positive(message = "Id should be positive") Long id
    ) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }


    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }
}