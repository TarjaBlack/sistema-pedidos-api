package com.example.sistemapedidos.api.dto;

import com.example.sistemapedidos.domain.ItemPedido;

import java.math.BigDecimal;

public record ItemPedidoDTO(
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        BigDecimal precoMomentaneo,
        BigDecimal subTotal
) {
    public ItemPedidoDTO(ItemPedido entity) {
        this(
                entity.getProduto().getId(),
                entity.getProduto().getNome(),
                entity.getQuantidade(),
                entity.getPrecoMomentaneo(),
                entity.getSubTotal()
        );
    }
}
