package com.example.graphql.dto;

import jakarta.validation.constraints.Positive;

public record BookUpdate(
        String name,
        @Positive Integer pageCount,
        Integer authorId
) {
}
