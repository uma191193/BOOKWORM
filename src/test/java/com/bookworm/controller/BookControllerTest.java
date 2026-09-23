package com.bookworm.controller;

import com.bookworm.dto.catalog.AuthorResponse;
import com.bookworm.dto.catalog.BookRequest;
import com.bookworm.dto.catalog.BookResponse;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.catalog.BookFormat;
import com.bookworm.security.JwtService;
import com.bookworm.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BookController")
class BookControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean  BookService  bookService;

    private UUID         bookId;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        AuthorResponse author = new AuthorResponse(authorId, "James Clear", "Bio", null);
        bookResponse = new BookResponse(
                bookId, "Atomic Habits", author, null, List.of(),
                "Description", BookFormat.PAPERBACK, "English",
                new BigDecimal("499.00"), "INR", null, "978-1",
                List.of("Self Help"), null, 4.8, 1000, null,
                LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /books: 200 public — no auth needed")
    void list_returns200() throws Exception {
        when(bookService.list(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(bookResponse)));

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Atomic Habits"));
    }

    @Test
    @DisplayName("GET /books/{id}: 200 for existing book")
    void getById_returns200() throws Exception {
        when(bookService.getById(bookId)).thenReturn(bookResponse);

        mockMvc.perform(get("/api/v1/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId.toString()));
    }

    @Test
    @DisplayName("GET /books/{id}: 404 for unknown book")
    void getById_returns404() throws Exception {
        when(bookService.getById(bookId))
                .thenThrow(new ResourceNotFoundException("Book not found: " + bookId));

        mockMvc.perform(get("/api/v1/books/{id}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /books/search: 200 with keyword")
    void search_returns200() throws Exception {
        when(bookService.search(eq("atomic"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(bookResponse)));

        mockMvc.perform(get("/api/v1/books/search").param("keyword", "atomic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /books: 201 when admin creates book")
    void create_returns201AsAdmin() throws Exception {
        BookRequest req = new BookRequest(
                "New Book", UUID.randomUUID(), null, List.of(), "Desc",
                BookFormat.EBOOK, "English", new BigDecimal("299.00"),
                "INR", null, null, List.of(), null, null);
        when(bookService.create(any())).thenReturn(bookResponse);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("POST /books: 403 when member tries to create")
    void create_returns403AsMember() throws Exception {
        BookRequest req = new BookRequest(
                "New Book", UUID.randomUUID(), null, List.of(), "Desc",
                BookFormat.EBOOK, "English", new BigDecimal("299.00"),
                "INR", null, null, List.of(), null, null);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /books/{id}: 204 when admin deletes")
    void delete_returns204AsAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/books/{id}", bookId))
                .andExpect(status().isNoContent());
    }
}
