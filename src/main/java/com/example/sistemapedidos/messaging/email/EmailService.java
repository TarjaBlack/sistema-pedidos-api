package com.example.sistemapedidos.messaging.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void enviarEmailStatus(String para, Long pedidoId, String novoStatus){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@marcoslabels.com.br");
        message.setTo(para);
        message.setSubject("Atualização do seu Pedido #" + pedidoId);
        message.setText("Olá!\n\nO status do seu pedido #" + pedidoId +
                " foi atualizado para: " + novoStatus +
                ".\n\nAcesse nosso portal para mais detalhes.");

        mailSender.send(message);
    }
}
