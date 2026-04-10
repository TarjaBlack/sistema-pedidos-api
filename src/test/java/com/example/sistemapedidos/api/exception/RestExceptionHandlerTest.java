package com.example.sistemapedidos.api.exception;

import com.example.sistemapedidos.api.CategoriaController;
import com.example.sistemapedidos.application.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// IMPORTANTE: Inclua os dois aqui para o Spring vincular o Handler ao Controller
@WebMvcTest({CategoriaController.class, RestExceptionHandler.class})
class RestExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService service;

    @Test
    @DisplayName("Deve capturar exceção genérica via RestExceptionHandler e retornar 500")
    void deveRetornar500QuandoErroGenericoNoService() throws Exception {
        // Arrange
        when(service.listarAtivas()).thenThrow(new RuntimeException("Erro interno inesperado"));

        // Act & Assert
        mockMvc.perform(get("/categorias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Aqui ele deve parar de lançar a ServletException
                .andExpect(jsonPath("$.mensagem").value("Erro interno inesperado"));
    }
}