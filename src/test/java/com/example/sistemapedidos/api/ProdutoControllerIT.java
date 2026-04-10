package com.example.sistemapedidos.api;

import com.example.sistemapedidos.api.dto.ProdutoRequestDTO;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import com.example.sistemapedidos.infrastructure.repositories.ProdutoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // Configura o MockMvc para testes com contexto completo
@ActiveProfiles("test")
@Transactional
class ProdutoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long categoriaId;

    @BeforeEach
    void setup() {
        // Limpar e popular dados necessários
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();

        Categoria cat = new Categoria();
        cat.setNome("Eletrônicos");
        cat.setAtivo(true);
        categoriaId = categoriaRepository.save(cat).getId();
    }

    @Test
    @DisplayName("Deve criar um produto e retornar 201 gravando no H2")
    void deveCriarProdutoReal() throws Exception {
        ProdutoRequestDTO request = new ProdutoRequestDTO("Smartphone", new BigDecimal("1500.00"), categoriaId);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Smartphone"));

        // Validação de sanidade: o banco tem exatamente 1 registro?
        assertEquals(1, produtoRepository.count());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar por ID inexistente no banco real")
    void deveRetornar404NoBancoReal() throws Exception {
        mockMvc.perform(get("/produtos/9999"))
                .andExpect(status().isNotFound());
    }
}