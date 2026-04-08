package com.example.sistemapedidos.api;

import com.example.sistemapedidos.api.mapper.ProdutoMapper;
import com.example.sistemapedidos.application.ProdutoService;
import com.example.sistemapedidos.domain.exception.EntidadeNaoEncontradaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProdutoController.class)
@ActiveProfiles("test")
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService service;

    @MockitoBean
    private ProdutoMapper mapper;

    @Test
    void deveRetornar404QuandoBuscarProdutoInexistente() throws Exception{

        Long idInexistente = 99L;
        when(service.buscarPorId(idInexistente))
                .thenThrow(new EntidadeNaoEncontradaException("Produto não encontrado"));

        mockMvc.perform(get("/produtos/" + idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.message").value("Produto não encontrado"));
    }

    @Test
    void deveRetornar400QuandoCriarProdutoInvalido() throws Exception{
        String jsonInvalido = "{}";

        mockMvc.perform(post("/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro de validação nos campos"));
    }

}