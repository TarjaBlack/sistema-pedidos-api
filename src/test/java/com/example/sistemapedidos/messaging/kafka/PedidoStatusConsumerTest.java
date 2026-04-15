package com.example.sistemapedidos.messaging.kafka;

import com.example.sistemapedidos.api.dto.PedidoResponseDTO;
import com.example.sistemapedidos.messaging.email.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoStatusConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PedidoStatusConsumer consumer;

    @Test
    @DisplayName("Deve chamar o serviço de e-mail quando receber uma mensagem do Kafka")
    void deveChamarEmailServiceAoReceberMensagem() {
        // Arrange
        PedidoResponseDTO pedidoDTO = new PedidoResponseDTO(
                1L,
                "PAGO",
                LocalDateTime.now(),
                new BigDecimal("100.00"),
                List.of()
        );

        // Act
        consumer.ouvirAlteracaoStatus(pedidoDTO);

        // Assert
        // Verificamos se o emailService foi chamado com os dados corretos
        verify(emailService, times(1))
                .enviarEmailStatus(eq("m.diazfarias@gmail.com"), eq(1L), eq("PAGO"));
    }

    @Test
    @DisplayName("Não deve quebrar o consumidor se o envio de e-mail falhar")
    void deveTratarErroAoFalharEnvioDeEmail() {
        // Arrange
        PedidoResponseDTO pedidoDTO = new PedidoResponseDTO(10L, "ENVIADO", LocalDateTime.now(), BigDecimal.ZERO, List.of());

        doThrow(new RuntimeException("Falha no servidor SMTP"))
                .when(emailService).enviarEmailStatus(anyString(), anyLong(), anyString());

        // Act & Assert
        // O consumer tem um try-catch, então ele não deve relançar a exceção (não quebra a fila)
        assertDoesNotThrow(() -> consumer.ouvirAlteracaoStatus(pedidoDTO));
        verify(emailService, times(1)).enviarEmailStatus(anyString(), anyLong(), anyString());
    }
}