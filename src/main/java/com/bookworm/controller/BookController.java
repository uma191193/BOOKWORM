package com.bookworm.controller;

import com.bookworm.dto.catalog.BookRequest;
import com.bookworm.dto.catalog.BookResponse;
import com.bookworm.model.catalog.BookFormat;
import com.bookworm.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "List all books (paginated)")
    public Page<BookResponse> list(Pageable pageable) {
        return bookService.list(pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books by title keyword")
    public Page<BookResponse> search(@RequestParam String keyword, Pageable pageable) {
        return bookService.search(keyword, pageable);
    }

    @GetMapping("/by-category/{categoryId}")
    @Operation(summary = "List books by category")
    public Page<BookResponse> byCategory(@PathVariable UUID categoryId, Pageable pageable) {
        return bookService.listByCategory(categoryId, pageable);
    }

    @GetMapping("/by-author/{authorId}")
    @Operation(summary = "List books by author")
    public Page<BookResponse> byAuthor(@PathVariable UUID authorId, Pageable pageable) {
        return bookService.listByAuthor(authorId, pageable);
    }

    @GetMapping("/by-format")
    @Operation(summary = "Filter books by format (PRINT / EBOOK / AUDIO)")
    public Page<BookResponse> byFormat(@RequestParam BookFormat format, Pageable pageable) {
        return bookService.listByFormat(format, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single book by ID")
    public BookResponse getById(@PathVariable UUID id) {
        return bookService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new book (admin only)")
    public BookResponse create(@Valid @RequestBody BookRequest request) {
        return bookService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a book (admin only)")
    public BookResponse update(@PathVariable UUID id, @Valid @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a book (admin only)")
    public void delete(@PathVariable UUID id) {
        bookService.delete(id);
    }
}
