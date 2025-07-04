package com.example.graphql.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AuthorCreate(
        @NotBlank String name,
        @NotNull @Positive Integer age
) {
}
