package com.example.graphql.controller;

import com.example.graphql.config.TestcontainersConfig;
import com.example.graphql.dto.AuthorCreate;
import com.example.graphql.dto.AuthorUpdate;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@Import(TestcontainersConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorControllerTest {

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
    @DisplayName("Get all authors with books")
    void authorsWithBooks() {
        Author author1 = authorRepository.save(new Author(null, "Author 1", 31, null));
        Author author2 = authorRepository.save(new Author(null, "Author 2", 63, null));

        bookRepository.save(new Book(null, "New Book1", 123, author1));
        bookRepository.save(new Book(null, "New Book2", 321, author2));

        author1 = authorRepository.findById(author1.getId()).get();
        author2 = authorRepository.findById(author2.getId()).get();

        List<Author> authorList = graphQlTester
                .documentName("getAuthorsWithBooks")
                .execute()
                .path("authors")
                .entityList(Author.class)
                .hasSize(2)
                .contains(author1, author2)
                .get();

        List<Book> books = authorList.stream()
                .map(Author::getBooks)
                .flatMap(List::stream)
                .toList();

        assertEquals(2, books.size());
    }

    @Test
    @DisplayName("Get all authors without books")
    void authorsWithoutBooks() {
        Author author1 = authorRepository.save(new Author(null, "Author 1", 31, null));
        Author author2 = authorRepository.save(new Author(null, "Author 2", 63, null));

        graphQlTester
                .documentName("getAuthorsWithoutBooks")
                .execute()
                .path("authors")
                .entityList(Author.class)
                .hasSize(2)
                .contains(author1, author2);
    }

    @Test
    @DisplayName("Get author by id with books")
    void authorByIdWithBooks() {
        Author author = authorRepository.save(new Author(null, "Author 1", 31, null));

        bookRepository.save(new Book(null, "New Book1", 123, author));
        bookRepository.save(new Book(null, "New Book2", 321, author));

        author = authorRepository.findById(author.getId()).get();

        Author authorResponse = graphQlTester
                .documentName("getAuthorByIdWithBooks")
                .variable("id", author.getId())
                .execute()
                .path("authorById")
                .entity(Author.class)
                .isEqualTo(author)
                .get();

        List<Book> books = authorResponse.getBooks();

        assertEquals(2, books.size());
    }

    @Test
    @DisplayName("Get author by id without books")
    void authorByIdWithoutBooks() {
        Author author = authorRepository.save(new Author(null, "Author 1", 31, null));

        graphQlTester
                .documentName("getAuthorByIdWithoutBooks")
                .variable("id", author.getId())
                .execute()
                .path("authorById")
                .entity(Author.class)
                .isEqualTo(author)
                .get();
    }

    @Test
    @DisplayName("Get author by id. NotFoundException")
    void authorByIdWithIncorrectId() {
        graphQlTester
                .documentName("getAuthorByIdWithoutBooks")
                .variable("id", 999)
                .execute()
                .errors()
                .expect(e -> Objects.equals(e.getMessage(), "Author not found"))
                .verify();
    }

    @Test
    @DisplayName("Add new author")
    void addNewAuthor() {
        AuthorCreate authorCreate = new AuthorCreate("New Author", 22);

        Author author = graphQlTester
                .documentName("addAuthor")
                .variable("input", authorCreate)
                .execute()
                .path("addAuthor")
                .entity(Author.class)
                .get();

        assertNotNull(author.getId());
        assertEquals(authorCreate.age(), author.getAge());
        assertEquals(authorCreate.name(), author.getName());
    }

    @Test
    @DisplayName("Update author")
    void updateAuthor() {
        Author author = authorRepository.save(new Author(null, "Author 1", 31, null));

        AuthorUpdate authorUpdate = new AuthorUpdate("New Author", 22);

        Author authorResponse = graphQlTester
                .documentName("updateAuthor")
                .variables(Map.of("id", author.getId(), "input", authorUpdate))
                .execute()
                .path("updateAuthor")
                .entity(Author.class)
                .get();

        assertEquals(authorUpdate.age(), authorResponse.getAge());
        assertEquals(authorUpdate.name(), authorResponse.getName());
        assertEquals(author.getId(), authorResponse.getId());
    }

    @Test
    @DisplayName("Update author. NotFoundException")
    void updateAuthorWithIncorrectId() {
        AuthorUpdate authorUpdate = new AuthorUpdate("New Author", 22);

        graphQlTester
                .documentName("updateAuthor")
                .variables(Map.of("id", 999, "input", authorUpdate))
                .execute()
                .errors()
                .expect(e -> Objects.equals(e.getMessage(), "Author not found"))
                .verify();
    }

    @Test
    @DisplayName("Delete author")
    void deleteAuthor() {
        Author author = authorRepository.save(new Author(null, "Author name", 33, null));

        graphQlTester
                .documentName("deleteAuthor")
                .variable("id", author.getId())
                .execute()
                .path("deleteAuthor")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("Delete author. NotFoundException")
    void deleteAuthorWithIncorrectId() {
        graphQlTester
                .documentName("deleteAuthor")
                .variable("id", 999)
                .execute()
                .errors()
                .expect(e -> Objects.equals(e.getMessage(), "Author not found"))
                .verify();
    }
}
