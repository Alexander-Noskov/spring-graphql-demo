package com.example.graphql.util;

import com.example.graphql.entity.Author;
import com.example.graphql.entity.Book;

import java.util.List;

public final class AuthorUtil {
    public static Author getJohnDoe() {
        return new Author(1, "John Doe", 33, List.of());
    }

    public static Author getJohnDoe(List<Book> books) {
        return new Author(1, "John Doe", 33, books);
    }

    public static Author getJaneDoe() {
        return new Author(2, "Jane Doe", 28, List.of());
    }

    public static Author getJaneDoe(List<Book> books) {
        return new Author(2, "Jane Doe", 28, books);
    }

    public static List<Author> getAllAuthors() {
        return List.of(getJohnDoe(), getJaneDoe());
    }

    public static List<Author> getAllAuthors(List<Book> books1, List<Book> books2) {
        return List.of(getJohnDoe(books1), getJaneDoe(books2));
    }
}
