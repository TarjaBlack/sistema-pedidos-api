package com.example.sistemapedidos.api.dto;

import jakarta.validation.constraints.NotNull;

public record StatusUpdateDTO(
        @NotNull(message = "O código do status é obrigatório")
        Integer status
) {}