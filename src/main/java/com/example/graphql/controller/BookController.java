package com.example.graphql.controller;

import com.example.graphql.dto.BookCreate;
import com.example.graphql.dto.BookUpdate;
import com.example.graphql.entity.Book;
import com.example.graphql.service.BookService;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @QueryMapping
    public List<Book> books(DataFetchingEnvironment env) {
        boolean loadAuthor = env.getSelectionSet().contains("author");
        return bookService.findAll(loadAuthor);
    }

    @QueryMapping
    public Book bookById(@Argument("id") Integer bookId, DataFetchingEnvironment env) {
        boolean loadAuthor = env.getSelectionSet().contains("author");
        return bookService.findById(bookId, loadAuthor);
    }

    @MutationMapping
    public Book addBook(@Valid @Argument("input") BookCreate bookCreate) {
        return bookService.createBook(bookCreate);
    }

    @MutationMapping
    public Book updateBook(@Argument("id") int id,
                           @Valid @Argument("input") BookUpdate bookUpdate
    ) {
        return bookService.updateBook(id, bookUpdate);
    }

    @MutationMapping
    public boolean deleteBook(@Argument int id) {
        return bookService.deleteBook(id);
    }

}
