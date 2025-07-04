package com.example.graphql.controller;

import com.example.graphql.config.TestcontainersConfig;
import com.example.graphql.dto.BookCreate;
import com.example.graphql.dto.BookUpdate;
import com.example.graphql.entity.Author;
import com.example.graphql.entity.Book;
import com.example.graphql.repository.AuthorRepository;
import com.example.graphql.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@Import(TestcontainersConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    @DisplayName("Get all books with authors")
    void booksWithAuthors() {
        Author author = authorRepository.save(new Author(null, "Author name", 33, null));

        Book book1 = bookRepository.save(new Book(null, "New Book1", 123, author));
        Book book2 = bookRepository.save(new Book(null, "New Book2", 321, author));

        graphQlTester
                .documentName("getBooksWithAuthors")
                .execute()
                .path("books")
                .entityList(Book.class)
                .hasSize(2)
                .contains(book1, book2);
    }

    @Test
    @DisplayName("Get all books without authors")
    void booksWithoutAuthors() {

        Book book1 = bookRepository.save(new Book(null, "New Book1", 123, null));
        Book book2 = bookRepository.save(new Book(null, "New Book2", 321, null));

        graphQlTester
                .documentName("getBooksWithoutAuthors")
                .execute()
                .path("books")
                .entityList(Book.class)
                .hasSize(2)
                .contains(book1, book2);
    }

    @Test
    @DisplayName("Get book by id with author")
    void bookByIdWithAuthors() {
        Author author = authorRepository.save(new Author(null, "Author name", 33, null));
        Book book = bookRepository.save(new Book(null, "New Book1", 123, author));

        graphQlTester
                .documentName("getBookByIdWithAuthor")
                .variable("id", book.getId())
                .execute()
                .path("bookById")
                .entity(Book.class)
                .isEqualTo(book);
    }

    @Test
    @DisplayName("Get book by id without author")
    void bookByIdWithoutAuthors() {
        Book book = bookRepository.save(new Book(null, "New Book1", 123, null));

        graphQlTester
                .documentName("getBookByIdWithoutAuthor")
                .variable("id", book.getId())
                .execute()
                .path("bookById")
                .entity(Book.class)
                .isEqualTo(book);
    }

    @Test
    @DisplayName("Get book by id. NotFoundException")
    void bookByIdExceptionNotFound() {
        graphQlTester
                .documentName("getBookByIdWithoutAuthor")
                .variable("id", 999)
                .execute()
                .errors()
                .expect(e -> Objects.equals(e.getMessage(), "Book not found"))
                .verify();
    }

    @Test
    @DisplayName("Add new book")
    void addNewBook() {
        Author author = authorRepository.save(new Author(null, "Author name", 33, null));

        BookCreate bookCreate = new BookCreate("New Book", 111, author.getId());

        Book book = graphQlTester
                .documentName("addBook")
                .variable("input", bookCreate)
                .execute()
                .path("addBook")
                .entity(Book.class)
                .get();

        assertNotNull(book.getId());
        assertEquals(bookCreate.name(), book.getName());
        assertEquals(bookCreate.pageCount(), book.getPageCount());
        assertEquals(author, book.getAuthor());
    }

    @Test
    @DisplayName("Add new book. NotFoundException")
    void addNewBookWithoutAuthor() {
        BookCreate bookCreate = new BookCreate("New Book", 111, 999);

        graphQlTester
                .documentName("addBook")
                .variable("input", bookCreate)
                .execute()
                .errors()
                .expect(e -> Objects.equals(e.getMessage(), "Author not found"))
                .verify();
    }

    @Test
    @DisplayName("Update book")
    void updateBook() {
        Author author = authorRepository.save(new Author(null, "Author name", 33, null));
        Book book = bookRepository.save(new Book(null, "New Book1", 123, author));

        BookUpdate bookUpdate = new BookUpdate("New Name", 333, null);

        Book updatedBook = graphQlTester
                .documentName("updateBook")
                .variables(Map.of("id", book.getId(), "input", bookUpdate))
                .execute()
                .path("updateBook")
                .entity(Book.class)
                .get();

        assertEquals(bookUpdate.name(), updatedBook.getName());
        assertEquals(bookUpdate.pageCount(), updatedBook.getPageCount());
        assertEquals(author, updatedBook.getAuthor());
        assertEquals(book.getId(), updatedBook.getId());
    }

    @Test
    @DisplayName("Update book. NotFoundException")
    void updateBookWithIncorrectId() {
        BookUpdate bookUpdate = new BookUpdate("New Name", 333, null);

        graphQlTester
                .documentName("updateBook")
                .variables(Map.of("id", 999, "input", bookUpdate))
                .execute()
                .errors()
                .expect(e -> Objects.equals(e.getMessage(), "Book not found"))
                .verify();
    }

    @Test
    @DisplayName("Delete book")
    void deleteBook() {
        Author author = authorRepository.save(new Author(null, "Author name", 33, null));
        Book book = bookRepository.save(new Book(null, "New Book1", 123, author));

        graphQlTester
                .documentName("deleteBook")
                .variable("id", book.getId())
                .execute()
                .path("deleteBook")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Delete book. NotFoundException")
    void deleteBookWithIncorrectId() {
        graphQlTester
                .documentName("deleteBook")
                .variable("id", 999)
                .execute()
                .errors()
                .expect(e -> Objects.equals(e.getMessage(), "Book not found"))
                .verify();
    }
}