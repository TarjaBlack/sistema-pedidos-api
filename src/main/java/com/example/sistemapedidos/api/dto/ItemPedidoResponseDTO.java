package com.example.sistemapedidos.api.dto;

import com.example.sistemapedidos.domain.ItemPedido;

import java.math.BigDecimal;
public record ItemPedidoResponseDTO(
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
    public ItemPedidoResponseDTO(ItemPedido item) {
        this(
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoMomentaneo(), // Usando o nome exato da sua entidade
                item.getSubTotal()
        );
    }
}