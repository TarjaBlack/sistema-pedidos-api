package com.example.sistemapedidos.domain;

import com.example.sistemapedidos.domain.enums.PedidoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Necessário para o @CreatedDate funcionar
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.ORDINAL) // Ou STRING, conforme seu padrão de banco
    private PedidoStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    private BigDecimal valorTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "id.pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens = new ArrayList<>();
}