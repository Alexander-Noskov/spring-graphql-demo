package com.example.graphql.service;

import com.example.graphql.dto.BookCreate;
import com.example.graphql.dto.BookUpdate;
import com.example.graphql.entity.Author;
import com.example.graphql.entity.Book;
import com.example.graphql.exception.NotFoundException;
import com.example.graphql.repository.AuthorRepository;
import com.example.graphql.repository.BookRepository;
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
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Get all books")
    void getAllBooks() {
        Author author = AuthorUtil.getJohnDoe();
        List<Book> books1 = BookUtil.getAllBooks(null, null);
        List<Book> books2 = BookUtil.getAllBooks(author, author);

        when(bookRepository.findAll()).thenReturn(books1);
        when(bookRepository.findAllWithAuthor()).thenReturn(books2);


        assertEquals(books1, bookService.findAll(false));
        assertEquals(books2, bookService.findAll(true));
    }

    @Test
    @DisplayName("Get book by id")
    void getBookById() {
        Author author = AuthorUtil.getJohnDoe();
        Book book1 = BookUtil.getFirstBook(null);
        Book book2 = BookUtil.getFirstBook(author);
        int bookId = book1.getId();
        int notFoundId = 999;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book1));
        when(bookRepository.findByIdWithAuthor(bookId)).thenReturn(Optional.of(book2));
        when(bookRepository.findById(notFoundId)).thenReturn(Optional.empty());
        when(bookRepository.findByIdWithAuthor(notFoundId)).thenReturn(Optional.empty());

        assertEquals(book1, bookService.findById(bookId, false));
        assertEquals(book2, bookService.findById(bookId, true));
        assertThrows(NotFoundException.class, () -> bookService.findById(notFoundId, false));
        assertThrows(NotFoundException.class, () -> bookService.findById(notFoundId, true));
    }

    @Test
    @DisplayName("Add book")
    void addBook() {
        Author author = AuthorUtil.getJohnDoe();
        Book book = BookUtil.getFirstBook(author);
        int authorId = author.getId();
        int notFoundId = 999;
        BookCreate bookCreate1 = new BookCreate(book.getName(), book.getPageCount(), authorId);
        BookCreate bookCreate2 = new BookCreate(book.getName(), book.getPageCount(), notFoundId);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorRepository.findById(notFoundId)).thenReturn(Optional.empty());

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        assertEquals(book, bookService.createBook(bookCreate1));
        assertThrows(NotFoundException.class, () -> bookService.createBook(bookCreate2));
    }

    @Test
    @DisplayName("Update book")
    void updateBook() {
        Author author = AuthorUtil.getJohnDoe();
        Book book1 = BookUtil.getFirstBook(author);
        Book book2 = BookUtil.getFirstBook(author);
        int bookId = book1.getId();
        int authorId = author.getId();
        int notFoundId = 999;
        BookUpdate bookUpdate1 = new BookUpdate("NewName", 333, authorId);
        BookUpdate bookUpdate2 = new BookUpdate(bookUpdate1.name(), bookUpdate1.pageCount(), notFoundId);

        book2.setName(bookUpdate1.name());
        book2.setPageCount(bookUpdate1.pageCount());

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(authorRepository.findById(notFoundId)).thenReturn(Optional.empty());

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertEquals(book2, bookService.updateBook(bookId, bookUpdate1));
        assertThrows(NotFoundException.class, () -> bookService.updateBook(bookId, bookUpdate2));
        assertThrows(NotFoundException.class, () -> bookService.updateBook(notFoundId, bookUpdate1));
    }

    @Test
    @DisplayName("Delete book")
    void deleteBook() {
        Author author = AuthorUtil.getJohnDoe();
        Book book = BookUtil.getFirstBook(author);
        int bookId = book.getId();
        int notFoundId = 999;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.findById(notFoundId)).thenReturn(Optional.empty());

        assertTrue(bookService.deleteBook(bookId));
        verify(bookRepository, times(1)).delete(book);

        assertThrows(NotFoundException.class, () -> bookService.deleteBook(notFoundId));
    }
}
