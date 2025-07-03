package com.example.graphql.controller;

import com.example.graphql.dto.AuthorCreate;
import com.example.graphql.dto.AuthorUpdate;
import com.example.graphql.entity.Author;
import com.example.graphql.service.AuthorService;
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
public class AuthorController {
    private final AuthorService authorService;

    @QueryMapping
    public List<Author> authors(DataFetchingEnvironment env) {
        boolean loadBooks = env.getSelectionSet().contains("books");
        return authorService.findAll(loadBooks);
    }

    @QueryMapping
    public Author authorById(@Argument("id") Integer authorId, DataFetchingEnvironment env) {
        boolean loadBooks = env.getSelectionSet().contains("books");
        return authorService.findById(authorId, loadBooks);
    }

    @MutationMapping
    public Author addAuthor(@Valid @Argument("input") AuthorCreate authorCreate) {
        return authorService.createAuthor(authorCreate);
    }

    @MutationMapping
    public Author updateAuthor(@Argument("id") int id,
                               @Valid @Argument("input") AuthorUpdate authorUpdate
    ) {
        return authorService.updateAuthor(id, authorUpdate);
    }

    @MutationMapping
    public boolean deleteAuthor(@Argument int id) {
        return authorService.deleteAuthor(id);
    }
}
