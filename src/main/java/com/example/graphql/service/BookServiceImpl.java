package com.example.graphql.service;

import com.example.graphql.dto.BookCreate;
import com.example.graphql.dto.BookUpdate;
import com.example.graphql.entity.Author;
import com.example.graphql.entity.Book;
import com.example.graphql.exception.NotFoundException;
import com.example.graphql.repository.AuthorRepository;
import com.example.graphql.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Override
    @Transactional(readOnly = true)
    public Book findById(Integer id, boolean loadAuthor) {
        return loadAuthor
                ? bookRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new NotFoundException("Book not found"))
                : bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll(boolean loadAuthor) {
        return loadAuthor
                ? bookRepository.findAllWithAuthor()
                : bookRepository.findAll();
    }

    @Override
    @Transactional
    public Book createBook(BookCreate input) {
        Author author = authorRepository.findById(input.authorId())
                .orElseThrow(() -> new NotFoundException("Author not found"));

        Book book = new Book();
        book.setName(input.name());
        book.setPageCount(input.pageCount());
        book.setAuthor(author);

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(Integer id, BookUpdate input) {
        Book book = findById(id, false);

        if (input.authorId() != null) {
            Author author = authorRepository.findById(input.authorId())
                    .orElseThrow(() -> new NotFoundException("Author not found"));
            book.setAuthor(author);
        }

        if (input.name() != null) book.setName(input.name());
        if (input.pageCount() != null) book.setPageCount(input.pageCount());

        return book;
    }

    @Override
    @Transactional
    public boolean deleteBook(Integer id) {
        Book book = findById(id, false);
        bookRepository.delete(book);
        return true;
    }

}
