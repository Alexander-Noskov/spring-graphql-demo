package com.example.graphql.service;

import com.example.graphql.dto.AuthorCreate;
import com.example.graphql.dto.AuthorUpdate;
import com.example.graphql.entity.Author;
import com.example.graphql.entity.Book;
import com.example.graphql.exception.NotFoundException;
import com.example.graphql.repository.AuthorRepository;
import com.example.graphql.util.AuthorUtil;
import com.example.graphql.util.BookUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;


    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    @DisplayName("Get all authors")
    void getAllAuthors() {
        List<Author> authorsWithoutBooks = AuthorUtil.getAllAuthors();
        List<Author> authorsWithBooks = AuthorUtil.getAllAuthors();
        Book books1 = BookUtil.getFirstBook(authorsWithBooks.getFirst());
        Book books2 = BookUtil.getSecondBook(authorsWithBooks.getLast());
        authorsWithBooks.getFirst().setBooks(List.of(books1));
        authorsWithBooks.getLast().setBooks(List.of(books2));

        when(authorRepository.findAll()).thenReturn(authorsWithoutBooks);
        when(authorRepository.findAllWithBooks()).thenReturn(authorsWithBooks);

        assertEquals(authorsWithoutBooks, authorService.findAll(false));
        assertEquals(authorsWithBooks, authorService.findAll(true));
    }

    @Test
    @DisplayName("Get author by id")
    void getAuthorById() {
        Author author1 = AuthorUtil.getJohnDoe();
        Author author2 = AuthorUtil.getJohnDoe();
        Book book = BookUtil.getFirstBook(author2);
        author2.setBooks(List.of(book));
        int authorId = author1.getId();
        int notFoundId = 999;

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author1));
        when(authorRepository.findWithBooks(authorId)).thenReturn(Optional.of(author2));
        when(authorRepository.findById(notFoundId)).thenReturn(Optional.empty());
        when(authorRepository.findWithBooks(notFoundId)).thenReturn(Optional.empty());

        assertEquals(author1, authorService.findById(authorId, false));
        assertEquals(author2, authorService.findById(authorId, true));
        assertThrows(NotFoundException.class, () -> authorService.findById(notFoundId, false));
        assertThrows(NotFoundException.class, () -> authorService.findById(notFoundId, true));
    }

    @Test
    @DisplayName("Add author")
    void addAuthor() {
        Author author1 = AuthorUtil.getJohnDoe();
        AuthorCreate authorCreate = new AuthorCreate(author1.getName(), author1.getAge());

        when(authorRepository.save(any(Author.class))).thenReturn(author1);

        assertEquals(author1, authorService.createAuthor(authorCreate));
    }

    @Test
    @DisplayName("Update author")
    void updateAuthor() {
        Author author1 = AuthorUtil.getJohnDoe();
        AuthorUpdate authorUpdate = new AuthorUpdate("Name", 45);
        Author author2 = AuthorUtil.getJohnDoe();
        author2.setName(authorUpdate.name());
        author2.setAge(authorUpdate.age());
        int authorId = author1.getId();
        int notFoundId = 999;

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author1));
        when(authorRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertEquals(author2, authorService.updateAuthor(authorId, authorUpdate));
        assertThrows(NotFoundException.class, () -> authorService.updateAuthor(notFoundId, authorUpdate));
    }

    @Test
    @DisplayName("Delete author")
    void deleteAuthor() {
        Author author1 = AuthorUtil.getJohnDoe();
        int authorId = author1.getId();
        int notFoundId = 999;

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author1));
        when(authorRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertTrue(authorService.deleteAuthor(authorId));
        verify(authorRepository, times(1)).delete(author1);

        assertThrows(NotFoundException.class, () -> authorService.deleteAuthor(notFoundId));
    }
}
