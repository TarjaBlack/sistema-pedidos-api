package com.example.sistemapedidos.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemRequestDTO (
        @NotNull
        @Positive
        Long produtoId,

        @NotNull
        @Positive
        Integer quantidade
){}
