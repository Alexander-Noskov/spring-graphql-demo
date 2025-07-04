package com.example.graphql.util;

import com.example.graphql.entity.Author;
import com.example.graphql.entity.Book;

import java.util.List;

public final class BookUtil {
    public static Book getFirstBook(Author author) {
        return new Book(1, "First Book", 123, author);
    }

    public static Book getSecondBook(Author author) {
        return new Book(2, "Second Book", 333, author);
    }

    public static List<Book> getAllBooks(Author author1, Author author2) {
        return List.of(getFirstBook(author1), getSecondBook(author2));
    }
}
