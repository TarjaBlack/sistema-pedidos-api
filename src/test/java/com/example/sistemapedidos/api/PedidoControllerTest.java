package com.example.sistemapedidos.api;

import com.example.sistemapedidos.api.dto.ItemRequestDTO;
import com.example.sistemapedidos.api.dto.PedidoDTO;
import com.example.sistemapedidos.api.dto.PedidoRequestDTO;
import com.example.sistemapedidos.api.dto.PedidoResponseDTO;
import com.example.sistemapedidos.application.PedidoService;
import com.example.sistemapedidos.domain.enums.PedidoStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    private PedidoRequestDTO pedidoRequest;
    private PedidoDTO pedidoResponse;

    @BeforeEach
    void setUp() {
        // Mock do Request
        ItemRequestDTO itemRequest = new ItemRequestDTO(1L, 2);
        pedidoRequest = new PedidoRequestDTO(1L, List.of(itemRequest));

        // Mock da Response
        pedidoResponse = new PedidoDTO(
                100L,
                LocalDateTime.now(),
                PedidoStatus.AGUARDANDO_PAGAMENTO,
                BigDecimal.valueOf(100.0),
                List.of()
        );
    }

    @Test
    @DisplayName("Deve retornar 201 ao inserir pedido com sucesso")
    void deveRetornar201AoInserirPedido() throws Exception {
        // 1. Arrange - Criar o DTO que o Service vai retornar
        // Ajuste os parâmetros conforme o construtor do seu Record
        PedidoResponseDTO response = new PedidoResponseDTO(
                1L,
                "AGUARDANDO_PAGAMENTO",
                LocalDateTime.now(),
                BigDecimal.ZERO,
                List.of()
        );

        when(pedidoService.inserir(any())).thenReturn(response);

        // 2. Act & Assert
        mockMvc.perform(post("/pedidos")
                        .content(objectMapper.writeValueAsString(pedidoRequest)) // Seu DTO de entrada
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"));
    }

    @Test
    void inserir_DeveRetornar400_QuandoClienteIdForNulo() throws Exception {
        // Pedido inválido sem clienteId
        PedidoRequestDTO requestInvalida = new PedidoRequestDTO(null, List.of(new ItemRequestDTO(1L, 2)));

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalida)))
                .andExpect(status().isBadRequest());
    }
}
