package com.example.sistemapedidos.api.dto;

import com.example.sistemapedidos.domain.Pedido;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Representação de saída de um pedido")
public record PedidoResponseDTO(
        @Schema(description = "ID único do pedido", example = "1")
        Long id,

        @Schema(description = "Status atual do pedido", example = "PAGO")
        String status,

        @Schema(description = "Data e hora em que o pedido foi realizado")
        LocalDateTime dataCriacao,

        @Schema(description = "Valor total do pedido", example = "250.50")
        BigDecimal valorTotal,

        @Schema(description = "Lista de itens incluídos no pedido")
        List<ItemPedidoResponseDTO> itens
) {
    // Construtor compacto para converter a Entidade Pedido para o DTO
    public PedidoResponseDTO(Pedido pedido) {
        this(
                pedido.getId(),
                pedido.getStatus().name(),
                pedido.getDataCriacao(),
                pedido.getValorTotal(),
                pedido.getItens() != null ?
                        pedido.getItens().stream().map(ItemPedidoResponseDTO::new).toList() :
                        List.of()
        );
    }
}