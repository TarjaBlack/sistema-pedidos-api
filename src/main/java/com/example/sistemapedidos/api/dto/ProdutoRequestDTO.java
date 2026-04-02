package com.example.sistemapedidos.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class ProdutoRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message="Categoria é obrigatória")
    private Long categoriaId;


}
