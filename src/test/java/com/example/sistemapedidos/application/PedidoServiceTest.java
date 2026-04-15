package com.example.sistemapedidos.application;

import com.example.sistemapedidos.api.dto.ItemRequestDTO;
import com.example.sistemapedidos.api.dto.PedidoRequestDTO;
import com.example.sistemapedidos.api.dto.PedidoResponseDTO;
import com.example.sistemapedidos.application.common.FinderService;
import com.example.sistemapedidos.domain.ItemPedido;
import com.example.sistemapedidos.domain.Pedido;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.domain.enums.PedidoStatus;
import com.example.sistemapedidos.domain.exception.BusinessException;
import com.example.sistemapedidos.infrastructure.repositories.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {
    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private KafkaTemplate<String, PedidoResponseDTO> kafkaTemplate;

    @Mock
    private FinderService finderService;

    @InjectMocks
    private PedidoService pedidoService;

    private PedidoRequestDTO pedidoRequest;

    @BeforeEach
    void setUp() {
        ItemRequestDTO item1 = new ItemRequestDTO(1L, 2);
        ItemRequestDTO item2 = new ItemRequestDTO(2L, 1);
        
        pedidoRequest = new PedidoRequestDTO(1L, List.of(item1, item2));
    }

    @Test
    @DisplayName("Deve buscar um pedido por ID com sucesso")
    void buscarPorId_DeveRetornarPedido_QuandoIdExiste() {
        // Arrange
        Long pedidoId = 100L;
        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        pedido.setStatus(PedidoStatus.AGUARDANDO_PAGAMENTO);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setValorTotal(new BigDecimal("150.00"));
        pedido.setItens(new ArrayList<>()); // Lista vazia para evitar NullPointerException no DTO

        when(finderService.pedidoOuFalhar(pedidoId)).thenReturn(pedido);

        // Act
        PedidoResponseDTO resultado = pedidoService.buscarPorId(pedidoId);

        // Assert
        assertNotNull(resultado);
        assertEquals(pedidoId, resultado.id());
        assertEquals(PedidoStatus.AGUARDANDO_PAGAMENTO.name(), resultado.status());
        assertEquals(new BigDecimal("150.00"), resultado.valorTotal());
        
        verify(finderService, times(1)).pedidoOuFalhar(pedidoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pedido com ID inexistente")
    void buscarPorId_DeveLancarExcecao_QuandoIdNaoExiste() {
        // Arrange
        Long pedidoId = 999L;
        String mensagemEsperada = "Pedido não encontrado";
        
        when(finderService.pedidoOuFalhar(pedidoId))
                .thenThrow(new EntityNotFoundException(mensagemEsperada));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> pedidoService.buscarPorId(pedidoId));

        assertEquals(mensagemEsperada, exception.getMessage());
        verify(finderService, times(1)).pedidoOuFalhar(pedidoId);
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
        assertEquals(0, new BigDecimal("200.00").compareTo(resultado.valorTotal()));

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
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> pedidoService.inserir(pedidoRequest));

        // Agora o símbolo 'exception' existe e pode ser validado
        assertEquals(mensagemEsperada, exception.getMessage());

        // Verifica que o Service chamou o FinderService
        verify(finderService, times(1)).produtoOuFalhar(anyLong());

        // Garante que o fluxo foi interrompido e o repositório NUNCA foi chamado
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve atualizar o status do pedido e enviar evento para o Kafka")
    void atualizarStatus_DeveAtualizarStatusEEnviarEvento() {
        // --- ARRANGE ---
        Long pedidoId = 1L;
        Integer novoStatusCodigo = 2; // Supondo que 2 seja PAGO
        PedidoStatus novoStatus = PedidoStatus.PAGO;

        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        pedido.setStatus(PedidoStatus.AGUARDANDO_PAGAMENTO);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setValorTotal(new BigDecimal("100.00"));
        pedido.setItens(new ArrayList<>());

        // Configura os mocks
        when(finderService.pedidoOuFalhar(pedidoId)).thenReturn(pedido);

        // O repository.save retorna o próprio pedido (estratégia comum em mocks)
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- ACT ---
        PedidoResponseDTO resultado = pedidoService.atualizarStatus(pedidoId, novoStatusCodigo);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals("PAGO", resultado.status());
        assertEquals(pedidoId, resultado.id());

        // Verificamos se o status foi realmente alterado na entidade
        assertEquals(novoStatus, pedido.getStatus());

        // VERIFICAÇÃO KAFKA: O ponto principal da atualização
        // Verificamos se o método send foi chamado uma vez para o tópico correto
        verify(kafkaTemplate, times(1)).send(
                eq("pedido-status-events"),
                eq(pedidoId.toString()),
                any(PedidoResponseDTO.class)
        );

        // Verifica se o save foi chamado
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar transição de status inválida")
    void atualizarStatus_DeveLancarExcecao_QuandoTransicaoInvalida() {
        // Arrange
        Long pedidoId = 1L;
        Pedido pedidoEntregue = new Pedido();
        pedidoEntregue.setStatus(PedidoStatus.ENTREGUE); // Status que impede mudança

        when(finderService.pedidoOuFalhar(pedidoId)).thenReturn(pedidoEntregue);

        // Act & Assert
        assertThrows(BusinessException.class, () ->
                pedidoService.atualizarStatus(pedidoId, 5) // Tentando cancelar um pedido entregue
        );

        // Verificação de segurança: O Kafka e o Repository NUNCA devem ser chamados
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar atualizar status de pedido inexistente")
    void atualizarStatus_DeveLancarExcecao_QuandoPedidoNaoExiste() {
        // Arrange
        Long pedidoId = 999L;
        when(finderService.pedidoOuFalhar(pedidoId))
                .thenThrow(new EntityNotFoundException("Pedido não encontrado"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                pedidoService.atualizarStatus(pedidoId, 2)
        );

        // Verificação de segurança: O Kafka e o Repository NUNCA devem ser chamados
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para código de status inválido")
    void atualizarStatus_DeveLancarExcecao_QuandoCodigoStatusInvalido() {
        // Arrange
        Long pedidoId = 1L;
        Pedido pedido = new Pedido();
        pedido.setStatus(PedidoStatus.AGUARDANDO_PAGAMENTO);

        when(finderService.pedidoOuFalhar(pedidoId)).thenReturn(pedido);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                pedidoService.atualizarStatus(pedidoId, 99) // Código 99 não existe no Enum
        );

        // Verificação de segurança: O Kafka e o Repository NUNCA devem ser chamados
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
        verify(pedidoRepository, never()).save(any());
    }
}
