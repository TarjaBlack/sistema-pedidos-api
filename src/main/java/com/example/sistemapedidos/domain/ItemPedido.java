package com.example.sistemapedidos.domain;

import com.example.sistemapedidos.domain.Pedido;
import com.example.sistemapedidos.domain.Produto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_item_pedido")
@Data
@NoArgsConstructor
public class ItemPedido {

    @EmbeddedId
    private ItemPedidoPK id = new ItemPedidoPK();

    private Integer quantidade;

    @Column(precision = 12, scale = 2)
    private BigDecimal precoMomentaneo;

    // Métodos delegados para facilitar o acesso (Importante para o DTO)
    public void setPedido(Pedido pedido) { id.setPedido(pedido); }

    @Transient // Indica que não é uma coluna, apenas um atalho
    public Pedido getPedido() { return id.getPedido(); }

    public void setProduto(Produto produto) { id.setProduto(produto); }

    @Transient
    public Produto getProduto() { return id.getProduto(); }

    // Alinhando o nome com o que o DTO espera (ou ajuste o DTO para usar este)
    public BigDecimal getPrecoUnitario() {
        return precoMomentaneo;
    }

    public BigDecimal getSubTotal(){
        if (precoMomentaneo == null || quantidade == null) return BigDecimal.ZERO;
        return precoMomentaneo.multiply(BigDecimal.valueOf(quantidade));
    }
}