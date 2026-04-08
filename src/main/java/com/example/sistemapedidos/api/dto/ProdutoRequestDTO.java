package com.example.sistemapedidos.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de um produto")
public class ProdutoRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Schema(example = "Teclado Mecânico RGB")
    private String nome;

    @Positive(message = "Preço deve ser maior que zero")
    @Schema(example = "250.00")
    private BigDecimal preco;

    @NotNull(message="Categoria é obrigatória")
    private Long categoriaId;

}
