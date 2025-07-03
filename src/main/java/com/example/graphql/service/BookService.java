package com.example.graphql.service;

import com.example.graphql.dto.BookCreate;
import com.example.graphql.dto.BookUpdate;
import com.example.graphql.entity.Book;

import java.util.List;

public interface BookService {
    Book findById(Integer id, boolean loadAuthor);

    List<Book> findAll(boolean loadAuthor);

    Book createBook(BookCreate book);

    Book updateBook(Integer id, BookUpdate book);

    boolean deleteBook(Integer id);
}
