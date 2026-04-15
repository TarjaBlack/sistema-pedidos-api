package com.example.sistemapedidos.messaging.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @Test
    void enviarEmailStatus_DeveConfigurarEEnviarEmailCorretamente() {
        // Arrange
        String para = "cliente@teste.com";
        Long pedidoId = 123L;
        String novoStatus = "ENVIADO";

        // Act
        emailService.enviarEmailStatus(para, pedidoId, novoStatus);

        // Assert
        verify(mailSender, times(1)).send(messageCaptor.capture());
        
        SimpleMailMessage mensagemEnviada = messageCaptor.getValue();
        
        assertNotNull(mensagemEnviada);
        assertEquals("noreply@marcoslabels.com.br", mensagemEnviada.getFrom());
        
        assertNotNull(mensagemEnviada.getTo());
        assertEquals(1, mensagemEnviada.getTo().length);
        assertEquals(para, mensagemEnviada.getTo()[0]);
        
        assertEquals("Atualização do seu Pedido #123", mensagemEnviada.getSubject());
        
        String textoEsperado = "Olá!\n\nO status do seu pedido #123 foi atualizado para: ENVIADO.\n\nAcesse nosso portal para mais detalhes.";
        assertEquals(textoEsperado, mensagemEnviada.getText());
    }
}
