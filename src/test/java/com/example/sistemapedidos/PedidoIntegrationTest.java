package com.example.sistemapedidos;

import com.example.sistemapedidos.infrastructure.repositories.PedidoRepository;
import com.example.sistemapedidos.domain.Pedido;
import com.example.sistemapedidos.domain.enums.PedidoStatus;
import com.example.sistemapedidos.api.dto.StatusUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev") // Garante que use as configs do Postgres/Kafka do seu yml de dev
class PedidoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    // Redirecionamos o teste para os containers que já estão ativos no seu Docker Desktop
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/pedidos_db");
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "root");
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
    }

    @Test
    @DisplayName("Fluxo Completo: Deve atualizar status, salvar no banco e disparar evento Kafka")
    void fluxoCompleto_DeveAtualizarEComunicar() throws Exception {
        // 1. Arrange: Criar um pedido inicial no banco real (Docker)
        Pedido pedido = new Pedido();
        pedido.setStatus(PedidoStatus.AGUARDANDO_PAGAMENTO);
        pedido.setValorTotal(new BigDecimal("150.00"));

        pedido.setDataCriacao(LocalDateTime.now());

        final Pedido pedidoSalvo = repository.save(pedido);
        final Long pedidoId = pedidoSalvo.getId();

        // Status 2 = PAGO (Certifique-se que o ID no seu Enum/Banco condiz com o DTO)
        StatusUpdateDTO updateDTO = new StatusUpdateDTO(2);

        // 2. Act: Chamar a API real via MockMvc
        mockMvc.perform(patch("/pedidos/{id}/status", pedidoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());

        // 3. Assert & Await: O processamento do Kafka/Consumidor ocorre em background
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            // Buscamos a versão atualizada do banco
            Pedido pedidoFinal = repository.findById(pedidoId)
                    .orElseThrow(() -> new AssertionError("Pedido não encontrado no banco"));

            assertEquals(PedidoStatus.PAGO, pedidoFinal.getStatus());
            assertNotNull(pedidoFinal.getDataAtualizacao(), "A data de atualização deve ter sido preenchida pelo Hibernate");
        });
    }
}