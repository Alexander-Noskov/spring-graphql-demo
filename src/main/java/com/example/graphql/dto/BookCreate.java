package com.example.graphql.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookCreate(
        @NotBlank String name,
        @Positive @NotNull Integer pageCount,
        @NotNull Integer authorId
) {}
