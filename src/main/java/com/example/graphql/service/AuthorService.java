package com.example.graphql.service;

import com.example.graphql.dto.AuthorCreate;
import com.example.graphql.dto.AuthorUpdate;
import com.example.graphql.entity.Author;

import java.util.List;

public interface AuthorService {
    Author findById(Integer id, boolean loadBooks);

    List<Author> findAll(boolean loadBooks);

    Author createAuthor(AuthorCreate author);

    Author updateAuthor(Integer id, AuthorUpdate author);

    boolean deleteAuthor(Integer id);
}
