package com.example.sistemapedidos.api.dto;

import com.example.sistemapedidos.domain.Pedido;
import com.example.sistemapedidos.domain.enums.PedidoStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoDTO(
        Long id,
        LocalDateTime instante,
        PedidoStatus status,
        BigDecimal total,
        List<ItemPedidoDTO> itens
) {
    public PedidoDTO(Pedido entity) {
        this(
                entity.getId(),
                entity.getDataCriacao(),
                entity.getStatus(),
                entity.getValorTotal(),
                entity.getItens()
                        .stream()
                        .map(ItemPedidoDTO::new)
                        .toList()
        );
    }

}
