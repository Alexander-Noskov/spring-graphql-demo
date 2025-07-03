package com.example.graphql.dto;

import jakarta.validation.constraints.Positive;

public record AuthorUpdate(
        String name,
        @Positive Integer age
) {
}
