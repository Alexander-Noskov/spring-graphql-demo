package com.example.graphql.service;

import com.example.graphql.dto.AuthorCreate;
import com.example.graphql.dto.AuthorUpdate;
import com.example.graphql.entity.Author;
import com.example.graphql.exception.NotFoundException;
import com.example.graphql.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    @Transactional(readOnly = true)
    public Author findById(Integer id, boolean loadBooks) {
        return loadBooks
                ? authorRepository.findWithBooks(id)
                .orElseThrow(() -> new NotFoundException("Author not found"))
                : authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> findAll(boolean loadBooks) {
        return loadBooks
                ? authorRepository.findAllWithBooks()
                : authorRepository.findAll();
    }

    @Override
    @Transactional
    public Author createAuthor(AuthorCreate author) {
        Author newAuthor = new Author();
        newAuthor.setAge(author.age());
        newAuthor.setName(author.name());
        return authorRepository.save(newAuthor);
    }

    @Override
    @Transactional
    public Author updateAuthor(Integer id, AuthorUpdate author) {
        Author updateAuthor = findById(id, false);
        if (author.age() != null) updateAuthor.setAge(author.age());
        if (author.name() != null) updateAuthor.setName(author.name());
        return updateAuthor;
    }

    @Override
    @Transactional
    public boolean deleteAuthor(Integer id) {
        Author author = findById(id, false);
        authorRepository.delete(author);
        return true;
    }
}
