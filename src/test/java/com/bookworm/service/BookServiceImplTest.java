package com.bookworm.service;

import com.bookworm.dto.catalog.BookRequest;
import com.bookworm.dto.catalog.BookResponse;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.catalog.Author;
import com.bookworm.model.catalog.Book;
import com.bookworm.model.catalog.BookFormat;
import com.bookworm.model.catalog.Publisher;
import com.bookworm.repository.AuthorRepository;
import com.bookworm.repository.BookRepository;
import com.bookworm.repository.CategoryRepository;
import com.bookworm.repository.PublisherRepository;
import com.bookworm.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookServiceImpl")
class BookServiceImplTest {

    @Mock BookRepository     bookRepository;
    @Mock AuthorRepository   authorRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock PublisherRepository publisherRepository;

    @InjectMocks BookServiceImpl bookService;

    private UUID   bookId;
    private UUID   authorId;
    private UUID   publisherId;
    private Author author;
    private Publisher publisher;
    private Book   book;

    @BeforeEach
    void setUp() {
        bookId      = UUID.randomUUID();
        authorId    = UUID.randomUUID();
        publisherId = UUID.randomUUID();

        author = Author.builder()
                .id(authorId).name("James Clear")
                .bio("Author bio").profileImageUrl("https://img.example.com/author.jpg")
                .build();

        publisher = Publisher.builder()
                .id(publisherId).name("Penguin").website("https://penguin.com")
                .build();

        book = Book.builder()
                .id(bookId).title("Atomic Habits")
                .author(author).publisher(publisher)
                .format(BookFormat.PAPERBACK)
                .price(new BigDecimal("499.00")).currencyCode("INR")
                .categories(new ArrayList<>()).tags(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ── getById ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById: returns BookResponse for existing book")
    void getById_returnsResponse() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookResponse response = bookService.getById(bookId);

        assertThat(response.id()).isEqualTo(bookId);
        assertThat(response.title()).isEqualTo("Atomic Habits");
        assertThat(response.price()).isEqualByComparingTo("499.00");
    }

    @Test
    @DisplayName("getById: throws ResourceNotFoundException when book missing")
    void getById_throwsWhenMissing() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getById(bookId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(bookId.toString());
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create: saves and returns BookResponse")
    void create_savesBook() {
        BookRequest req = new BookRequest(
                "Atomic Habits", authorId, publisherId, List.of(),
                "Description", BookFormat.PAPERBACK, "English",
                new BigDecimal("499.00"), "INR", null, "978-1",
                List.of("Self Help"), null, null);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));
        when(categoryRepository.findAllById(any())).thenReturn(List.of());
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId(bookId);
            b.setCreatedAt(LocalDateTime.now());
            return b;
        });

        BookResponse response = bookService.create(req);

        assertThat(response.title()).isEqualTo("Atomic Habits");
        assertThat(response.format()).isEqualTo(BookFormat.PAPERBACK);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("create: throws when author not found")
    void create_throwsWhenAuthorMissing() {
        BookRequest req = new BookRequest(
                "Atomic Habits", authorId, null, null, null,
                BookFormat.PAPERBACK, "English", new BigDecimal("499.00"),
                "INR", null, null, null, null, null);

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.create(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found");
    }

    // ── list ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("list: delegates to repository and maps results")
    void list_mapsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(book)));

        var page = bookService.list(pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst().title()).isEqualTo("Atomic Habits");
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete: removes existing book")
    void delete_removesBook() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        bookService.delete(bookId);

        verify(bookRepository).delete(book);
    }

    @Test
    @DisplayName("delete: throws when book missing")
    void delete_throwsWhenMissing() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.delete(bookId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
