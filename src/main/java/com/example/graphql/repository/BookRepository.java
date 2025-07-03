package com.example.graphql.repository;

import com.example.graphql.entity.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    @EntityGraph(attributePaths = "author")
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdWithAuthor(Integer id);

    @EntityGraph(attributePaths = "author")
    @Query("SELECT b FROM Book b")
    List<Book> findAllWithAuthor();
}
