package com.example.sistemapedidos.api.dto;

import com.example.sistemapedidos.domain.Produto;
import lombok.*;

import java.math.BigDecimal;


public record ProdutoResponseDTO(
    Long id,
    String nome,
    BigDecimal preco,
    String nomeCategoria
){
    public ProdutoResponseDTO(Produto entity){
        this(
                entity.getId(),
                entity.getNome(),
                entity.getPreco(),
                entity.getCategoria().getNome()
        );
    }
}
