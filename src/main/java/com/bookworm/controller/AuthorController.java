package com.bookworm.controller;

import com.bookworm.dto.catalog.AuthorRequest;
import com.bookworm.dto.catalog.AuthorResponse;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.catalog.Author;
import com.bookworm.repository.AuthorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
@Tag(name = "Authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    @GetMapping
    @Operation(summary = "List all authors")
    public List<AuthorResponse> listAll() {
        return authorRepository.findAll().stream()
                .map(a -> new AuthorResponse(a.getId(), a.getName(), a.getBio(), a.getProfileImageUrl()))
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an author by ID")
    public AuthorResponse getById(@PathVariable UUID id) {
        Author a = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
        return new AuthorResponse(a.getId(), a.getName(), a.getBio(), a.getProfileImageUrl());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create an author (admin)")
    public AuthorResponse create(@Valid @RequestBody AuthorRequest request) {
        Author a = Author.builder()
                .name(request.name())
                .bio(request.bio())
                .profileImageUrl(request.profileImageUrl())
                .build();
        Author saved = authorRepository.save(a);
        return new AuthorResponse(saved.getId(), saved.getName(), saved.getBio(), saved.getProfileImageUrl());
    }
}
