package com.example.sistemapedidos.messaging.kafka;

import com.example.sistemapedidos.api.dto.PedidoResponseDTO;
import com.example.sistemapedidos.messaging.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PedidoStatusConsumer {

    private final EmailService emailService;

    public PedidoStatusConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "pedido-status-events",  groupId = "grupo-pedidos")
    public void ouvirAlteracaoStatus(PedidoResponseDTO pedido){
        log.info("🔔 [NOTIFICAÇÃO] Pedido ID: {} atualizado para o status: {}", pedido.id(), pedido.status());

        try {
            //Aqui o dado vai vir do bd
            String emailCliente = "m.diazfarias@gmail.com";

            emailService.enviarEmailStatus(emailCliente, pedido.id(), pedido.status());
            log.info("✅ E-mail enviado com sucesso para o pedido #{}", pedido.id());
        } catch (Exception e) {
            log.error("❌ Erro ao enviar e-mail para o pedido #{}: {}", pedido.id(), e.getMessage());
        }

    }
}
