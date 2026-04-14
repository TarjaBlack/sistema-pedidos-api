package com.example.sistemapedidos.application;


import com.example.sistemapedidos.api.dto.ItemRequestDTO;
import com.example.sistemapedidos.api.dto.PedidoRequestDTO;
import com.example.sistemapedidos.api.dto.PedidoResponseDTO;
import com.example.sistemapedidos.application.common.FinderService;
import com.example.sistemapedidos.domain.ItemPedido;
import com.example.sistemapedidos.domain.Pedido;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.domain.enums.PedidoStatus;
import com.example.sistemapedidos.infrastructure.repositories.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository repository;
    private final FinderService finderService;

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long id, Integer novoStatusCodigo) {
        // Busca via FinderService (centraliza o 404)
        Pedido pedido = finderService.pedidoOuFalhar(id);

        // Converte o código para Enum (valida internamente no valueOf)
        PedidoStatus novoStatus = PedidoStatus.valueOf(novoStatusCodigo);

        // Regra de Negócio: Impede alteração de pedidos finalizados
        validarTransicao(pedido.getStatus(), novoStatus);

        pedido.setStatus(novoStatus);

        // Salva e já retorna convertido para Record
        return new PedidoResponseDTO(repository.save(pedido));
    }

    @Transactional
    public PedidoResponseDTO inserir(PedidoRequestDTO dto) {
        // 1. Instanciar o pedido
        Pedido pedido = new Pedido();
        pedido.setStatus(PedidoStatus.AGUARDANDO_PAGAMENTO);
        pedido.setDataCriacao(LocalDateTime.now());

        // 2. Processar os itens vindos do DTO
        for (ItemRequestDTO itemDto : dto.itens()) {
            // Busca o produto (se não achar, o FinderService já lança a exceção correta)
            Produto produto = finderService.produtoOuFalhar(itemDto.produtoId());

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido); // Vincula ao pedido atual
            item.setProduto(produto);
            item.setQuantidade(itemDto.quantidade());
            item.setPrecoMomentaneo(produto.getPreco()); // "Congela" o preço

            pedido.getItens().add(item);
        }

        // 3. Calcular o total antes de salvar
        recalcularValorTotal(pedido);

        // 4. Salvar (O CascadeType.ALL no Pedido salvará os itens automaticamente)
        pedido = repository.save(pedido);

        return new PedidoResponseDTO(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = finderService.pedidoOuFalhar(id);
        return new PedidoResponseDTO(pedido);
    }

    private void recalcularValorTotal(Pedido pedido) {
        BigDecimal total = pedido.getItens().stream()
                .map(ItemPedido::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorTotal(total);
    }

    private void validarTransicao(PedidoStatus atual, PedidoStatus proximo) {
        if (atual == PedidoStatus.CANCELADO || atual == PedidoStatus.ENTREGUE) {
            throw new IllegalStateException("Não é possível alterar o status de um pedido já finalizado.");
        }
    }
}
