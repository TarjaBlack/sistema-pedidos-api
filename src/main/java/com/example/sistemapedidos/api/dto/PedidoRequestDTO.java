package com.example.sistemapedidos.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequestDTO(
        @NotNull(message = "O cliente é obrigatório")
        Long clienteId,

        List<ItemRequestDTO> itens){}

