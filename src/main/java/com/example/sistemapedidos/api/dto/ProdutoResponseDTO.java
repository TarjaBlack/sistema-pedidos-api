package com.example.sistemapedidos.api.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProdutoResponseDTO {
    private Long id;
    private String nome;
    private BigDecimal preco;
    private CategoriaDTO categoria;
}
