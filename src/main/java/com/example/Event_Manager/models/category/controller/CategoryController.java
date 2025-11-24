package com.example.Event_Manager.models.category.controller;

import com.example.Event_Manager.models._util.annotations.IsAdmin;
import com.example.Event_Manager.models.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.models.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import com.example.Event_Manager.models.category.service.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryDTO updateCategoryDTO
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, updateCategoryDTO));
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }


    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}