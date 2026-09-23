package com.bookworm.controller;

import com.bookworm.dto.catalog.CategoryRequest;
import com.bookworm.dto.catalog.CategoryResponse;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.catalog.Category;
import com.bookworm.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    @Operation(summary = "List all categories")
    public List<CategoryResponse> listAll() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getParentCategoryId()))
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID")
    public CategoryResponse getById(@PathVariable UUID id) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        return new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getParentCategoryId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a category (admin)")
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        Category c = Category.builder()
                .name(request.name())
                .slug(request.slug())
                .parentCategoryId(request.parentCategoryId())
                .build();
        Category saved = categoryRepository.save(c);
        return new CategoryResponse(saved.getId(), saved.getName(), saved.getSlug(), saved.getParentCategoryId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a category (admin)")
    public void delete(@PathVariable UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
