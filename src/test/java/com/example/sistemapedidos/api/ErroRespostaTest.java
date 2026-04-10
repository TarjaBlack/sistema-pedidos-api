package com.example.sistemapedidos.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErroRespostaTest {

    @Test
    @DisplayName("Deve garantir a integridade dos dados no Record de Erro")
    void deveValidarRecord() {
        // Arrange
        int statusEsperado = 404;
        String mensagemEsperada = "Recurso não encontrado";

        // Act
        ErroResposta erro = new ErroResposta(statusEsperado, mensagemEsperada);

        // Assert
        assertEquals(statusEsperado, erro.Status());
        assertEquals(mensagemEsperada, erro.mensagem());
    }
}