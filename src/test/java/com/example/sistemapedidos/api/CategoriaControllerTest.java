package com.example.sistemapedidos.api;

import com.example.sistemapedidos.api.dto.CategoriaDTO;
import com.example.sistemapedidos.application.CategoriaService;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.domain.exception.EntidadeNaoEncontradaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService service;

    // Instanciado manualmente para evitar erro de Bean no contexto WebMvcTest
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Deve listar categorias ativas com sucesso")
    void deveListarCategorias() throws Exception {
        when(service.listarAtivas()).thenReturn(List.of(new Categoria(1L, "Teste", true)));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Teste"));
    }

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void deveCriarCategoria() throws Exception {
        CategoriaDTO dto = new CategoriaDTO(null, "Nova Categoria");
        Categoria categoriaSalva = new Categoria(1L, "Nova Categoria", true);

        when(service.salvar(any(CategoriaDTO.class))).thenReturn(categoriaSalva);

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar criar categoria com nome inválido")
    void deveRetornarErroValidacaoAoCriar() throws Exception {
        // Testando a branch de validação do @NotBlank
        CategoriaDTO dtoInvalido = new CategoriaDTO(null, "");

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void deveAtualizarCategoria() throws Exception {
        CategoriaDTO dto = new CategoriaDTO(1L, "Nome Atualizado");
        Categoria categoriaAtualizada = new Categoria(1L, "Nome Atualizado", true);

        when(service.atualizar(eq(1L), any(CategoriaDTO.class))).thenReturn(categoriaAtualizada);

        mockMvc.perform(put("/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Atualizado"));
    }

    @Test
    @DisplayName("Deve retornar 204 ao realizar delete lógico")
    void deveDeletarCategoria() throws Exception {
        // Uso do doNothing para métodos void
        doNothing().when(service).excluirLogico(1L);

        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).excluirLogico(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 quando a categoria não for encontrada")
    void deveRetornar404AoBuscarInexistente() throws Exception {
        // Cobre a branch de erro/exceção
        when(service.atualizar(eq(99L), any(CategoriaDTO.class)))
                .thenThrow(new EntidadeNaoEncontradaException("Categoria inexistente"));

        CategoriaDTO dto = new CategoriaDTO(99L, "Inexistente");

        mockMvc.perform(put("/categorias/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}