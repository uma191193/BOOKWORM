package com.bookworm.service.impl;

import com.bookworm.dto.catalog.*;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.catalog.Author;
import com.bookworm.model.catalog.Book;
import com.bookworm.model.catalog.BookFormat;
import com.bookworm.model.catalog.Category;
import com.bookworm.model.catalog.Publisher;
import com.bookworm.repository.AuthorRepository;
import com.bookworm.repository.BookRepository;
import com.bookworm.repository.CartItemRepository;
import com.bookworm.repository.CategoryRepository;
import com.bookworm.repository.OrderItemRepository;
import com.bookworm.repository.PublisherRepository;
import com.bookworm.repository.RecommendationRepository;
import com.bookworm.repository.ReviewRepository;
import com.bookworm.repository.WishlistItemRepository;
import com.bookworm.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Keeps Hibernate session open across all read operations
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final ReviewRepository reviewRepository;
    private final CartItemRepository cartItemRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final RecommendationRepository recommendationRepository;

    @Override
    @Transactional
    public BookResponse create(BookRequest request) {
        Book book = buildBook(new Book(), request);
        return toResponse(bookRepository.save(book));
    }

    @Override
    public BookResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public Page<BookResponse> list(Pageable pageable) {
        return bookRepository.findAllWithAssociations(pageable).map(this::toResponse);
    }

    @Override
    public Page<BookResponse> search(String keyword, Pageable pageable) {
        return bookRepository.searchByTitle(keyword, pageable).map(this::toResponse);
    }

    @Override
    public Page<BookResponse> listByCategory(UUID categoryId, Pageable pageable) {
        return bookRepository.findByCategoryId(categoryId, pageable).map(this::toResponse);
    }

    @Override
    public Page<BookResponse> listByAuthor(UUID authorId, Pageable pageable) {
        return bookRepository.findByAuthorId(authorId, pageable).map(this::toResponse);
    }

    @Override
    public Page<BookResponse> listByFormat(BookFormat format, Pageable pageable) {
        return bookRepository.findByFormatWithAssociations(format, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public BookResponse update(UUID id, BookRequest request) {
        Book book = findOrThrow(id);
        return toResponse(bookRepository.save(buildBook(book, request)));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        findOrThrow(id); // validate existence first
        reviewRepository.deleteByBookId(id);
        cartItemRepository.deleteByBookId(id);
        wishlistItemRepository.deleteByBookId(id);
        orderItemRepository.deleteByBookId(id);
        recommendationRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Book buildBook(Book book, BookRequest req) {
        Author author = authorRepository.findById(req.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + req.authorId()));
        Publisher publisher = req.publisherId() != null
                ? publisherRepository.findById(req.publisherId()).orElse(null)
                : null;
        List<Category> categories = req.categoryIds() != null
                ? categoryRepository.findAllById(req.categoryIds())
                : List.of();

        book.setTitle(req.title());
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setCategories(categories);
        book.setDescription(req.description());
        book.setFormat(req.format());
        book.setLanguage(req.language());
        book.setPrice(req.price());
        if (req.currencyCode() != null) book.setCurrencyCode(req.currencyCode());
        book.setCoverImageUrl(req.coverImageUrl());
        book.setIsbn(req.isbn());
        if (req.tags() != null) book.setTags(req.tags());
        book.setTentativeDeliveryDate(req.tentativeDeliveryDate());
        book.setStoreId(req.storeId());
        return book;
    }

    private Book findOrThrow(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    @Override
    public BookResponse toResponse(Book book) {
        AuthorResponse authorResponse = book.getAuthor() == null ? null :
                new AuthorResponse(book.getAuthor().getId(), book.getAuthor().getName(),
                        book.getAuthor().getBio(), book.getAuthor().getProfileImageUrl());
        PublisherResponse publisherResponse = book.getPublisher() == null ? null :
                new PublisherResponse(book.getPublisher().getId(), book.getPublisher().getName(),
                        book.getPublisher().getWebsite());
        List<CategoryResponse> categoryResponses = book.getCategories() == null ? List.of() :
                book.getCategories().stream()
                        .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getParentCategoryId()))
                        .toList();
        List<String> tags = book.getTags() == null ? List.of() : List.copyOf(book.getTags());
        return new BookResponse(book.getId(), book.getTitle(), authorResponse, publisherResponse,
                categoryResponses, book.getDescription(), book.getFormat(), book.getLanguage(),
                book.getPrice(), book.getCurrencyCode(), book.getCoverImageUrl(), book.getIsbn(),
                tags, book.getTentativeDeliveryDate(), book.getAverageRating(),
                book.getSalesCount(), book.getStockCount(), book.getStoreId(), book.getCreatedAt());
    }

}