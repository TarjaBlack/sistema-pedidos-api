package com.example.sistemapedidos.application;

import com.example.sistemapedidos.api.dto.ItemRequestDTO;
import com.example.sistemapedidos.api.dto.PedidoDTO;
import com.example.sistemapedidos.api.dto.PedidoRequestDTO;
import com.example.sistemapedidos.api.dto.PedidoResponseDTO;
import com.example.sistemapedidos.application.common.FinderService;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.domain.ItemPedido;
import com.example.sistemapedidos.domain.Pedido;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.domain.enums.PedidoStatus;
import com.example.sistemapedidos.infrastructure.repositories.PedidoRepository;
import com.example.sistemapedidos.infrastructure.repositories.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {
    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private FinderService finderService; // O mock que estava vindo nulo

    @InjectMocks
    private PedidoService pedidoService; // Mockito injetará os mocks acima aqui

    private PedidoRequestDTO pedidoRequest;
    private Produto produto1;
    private Produto produto2;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria(1L, "Informática", true);
        produto1 = new Produto(1L, "Mouse", BigDecimal.valueOf(50.0), categoria, true);
        produto2 = new Produto(2L, "Teclado", BigDecimal.valueOf(100.0), categoria, true);

        ItemRequestDTO item1 = new ItemRequestDTO(1L, 2);
        ItemRequestDTO item2 = new ItemRequestDTO(2L, 1);
        
        pedidoRequest = new PedidoRequestDTO(1L, List.of(item1, item2));
    }

    @Test
    @DisplayName("Deve salvar um pedido com itens e retornar o DTO com dados calculados")
    void inserir_DeveSalvarERetornarPedidoComSucesso() {
        // --- ARRANGE (Preparação) ---

        // 1. Instanciar produtos com preços para o cálculo do total
        Produto produto1 = new Produto();
        produto1.setId(1L);
        produto1.setNome("Produto A");
        produto1.setPreco(new BigDecimal("50.00"));

        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setNome("Produto B");
        produto2.setPreco(new BigDecimal("100.00"));

        // 2. Configurar o FinderService para retornar esses produtos
        when(finderService.produtoOuFalhar(1L)).thenReturn(produto1);
        when(finderService.produtoOuFalhar(2L)).thenReturn(produto2);

        // 3. Criar o RequestDTO (2 unidades do Prod1 + 1 unidade do Prod2 = 200.00)
        ItemRequestDTO item1 = new ItemRequestDTO(1L, 2);
        ItemRequestDTO item2 = new ItemRequestDTO(2L, 1);
        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO(1L, List.of(item1, item2));

        // 4. Simular o objeto que o Repository retornaria após o save
        Pedido pedidoSalvo = new Pedido();
        pedidoSalvo.setId(100L);
        pedidoSalvo.setStatus(PedidoStatus.AGUARDANDO_PAGAMENTO);
        pedidoSalvo.setDataCriacao(LocalDateTime.now());

        // IMPORTANTE: Criar os itens vinculados para o DTO de resposta não vir vazio
        ItemPedido ip1 = new ItemPedido();
        ip1.setProduto(produto1);
        ip1.setQuantidade(2);
        ip1.setPrecoMomentaneo(produto1.getPreco());

        ItemPedido ip2 = new ItemPedido();
        ip2.setProduto(produto2);
        ip2.setQuantidade(1);
        ip2.setPrecoMomentaneo(produto2.getPreco());

        pedidoSalvo.setItens(new ArrayList<>(List.of(ip1, ip2)));
        pedidoSalvo.setValorTotal(new BigDecimal("200.00"));

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvo);

        // --- ACT (Execução) ---
        PedidoResponseDTO resultado = pedidoService.inserir(pedidoRequest);

        // --- ASSERT (Verificação) ---
        assertNotNull(resultado);
        assertEquals(100L, resultado.id());
        assertEquals("AGUARDANDO_PAGAMENTO", resultado.status());
        assertEquals(2, resultado.itens().size());

        // Validação do cálculo (200.00)
        // Usamos compareTo == 0 para ignorar diferenças de escala (200.0 vs 200.00)
        assertTrue(new BigDecimal("200.00").compareTo(resultado.valorTotal()) == 0);

        // Verifica se as dependências foram chamadas corretamente
        verify(finderService, times(1)).produtoOuFalhar(1L);
        verify(finderService, times(1)).produtoOuFalhar(2L);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void inserir_DeveLancarExcecaoQuandoProdutoNaoEncontrado() {
        // Arrange
        String mensagemEsperada = "Produto não encontrado";
        when(finderService.produtoOuFalhar(anyLong()))
                .thenThrow(new EntityNotFoundException(mensagemEsperada));

        // Act & Assert
        // Capturamos a exceção retornada pelo assertThrows na variável 'exception'
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            pedidoService.inserir(pedidoRequest);
        });

        // Agora o símbolo 'exception' existe e pode ser validado
        assertEquals(mensagemEsperada, exception.getMessage());

        // Verifica que o Service chamou o FinderService
        verify(finderService, times(1)).produtoOuFalhar(anyLong());

        // Garante que o fluxo foi interrompido e o repositório NUNCA foi chamado
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
}
