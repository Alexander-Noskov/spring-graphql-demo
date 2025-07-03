package com.example.graphql.repository;

import com.example.graphql.entity.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    @EntityGraph(attributePaths = "books")
    @Query("SELECT a FROM Author a WHERE a.id = :id")
    Optional<Author> findWithBooks(@Param("id") Integer id);

    @EntityGraph(attributePaths = "books")
    @Query("SELECT a FROM Author a")
    List<Author> findAllWithBooks();
}
