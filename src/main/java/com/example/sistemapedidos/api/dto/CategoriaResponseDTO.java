package com.example.sistemapedidos.api.dto;

import com.example.sistemapedidos.domain.Categoria;

public record CategoriaResponseDTO(Long id, String nome, boolean ativo) {
    public CategoriaResponseDTO(Categoria entity){
        this(
                entity.getId(),
                entity.getNome(),
                entity.isAtivo()
        );
    }
}
