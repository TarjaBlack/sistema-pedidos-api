package com.example.sistemapedidos.api;

import com.example.sistemapedidos.api.dto.ProdutoRequestDTO;
import com.example.sistemapedidos.api.dto.ProdutoResponseDTO;
import com.example.sistemapedidos.api.mapper.ProdutoMapper;
import com.example.sistemapedidos.application.ProdutoService;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.domain.exception.EntidadeNaoEncontradaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.sistemapedidos.domain.Categoria;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProdutoController.class)
@ActiveProfiles("test")
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProdutoService service;

    @MockitoBean
    private ProdutoMapper mapper;

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorIdComSucesso() throws Exception {
        Long id = 1L;
        Produto produto = new Produto(id, "Teclado", BigDecimal.valueOf(100), new Categoria(), true);
        ProdutoResponseDTO response = new ProdutoResponseDTO(id, "Teclado", BigDecimal.valueOf(100), null);

        when(service.buscarPorId(id)).thenReturn(produto);
        when(mapper.toProdutoResponseDTO(produto)).thenReturn(response);

        mockMvc.perform(get("/produtos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Teclado"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar produto inexistente")
    void deveRetornar404QuandoBuscarProdutoInexistente() throws Exception {
        Long idInexistente = 99L;
        when(service.buscarPorId(idInexistente))
                .thenThrow(new EntidadeNaoEncontradaException("Produto não encontrado"));

        mockMvc.perform(get("/produtos/" + idInexistente))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() throws Exception {
        Long id = 1L;
        ProdutoRequestDTO request = new ProdutoRequestDTO("Mouse Gamer", BigDecimal.valueOf(150), 1L);
        Produto produtoAtualizado = new Produto(id, "Mouse Gamer", BigDecimal.valueOf(150), new Categoria(), true);
        ProdutoResponseDTO response = new ProdutoResponseDTO(id, "Mouse Gamer", BigDecimal.valueOf(150), null);

        when(service.atualizar(eq(id), any(ProdutoRequestDTO.class))).thenReturn(produtoAtualizado);
        when(mapper.toProdutoResponseDTO(any())).thenReturn(response);

        mockMvc.perform(put("/produtos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Mouse Gamer"));
    }

    @Test
    @DisplayName("Deve realizar delete lógico com sucesso (204)")
    void deveDeletarProdutoComSucesso() throws Exception {
        Long id = 1L;
        doNothing().when(service).excluirLogico(id);

        mockMvc.perform(delete("/produtos/" + id))
                .andExpect(status().isNoContent());

        verify(service, times(1)).excluirLogico(id);
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() throws Exception {
        ProdutoRequestDTO request = new ProdutoRequestDTO("Mouse", BigDecimal.TEN, 1L);
        Produto produtoSimulado = new Produto(1L, "Mouse", BigDecimal.TEN, new Categoria(), true);
        ProdutoResponseDTO responseSimulado = new ProdutoResponseDTO(1L, "Mouse", BigDecimal.TEN, null);

        when(service.salvar(any())).thenReturn(produtoSimulado);
        when(mapper.toProdutoResponseDTO(any())).thenReturn(responseSimulado);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Mouse"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao enviar dados inválidos no POST")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        ProdutoRequestDTO requestInvalido = new ProdutoRequestDTO("", BigDecimal.valueOf(-1), null);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar todos os produtos ativos")
    void deveListarProdutos() throws Exception {
        when(service.listarAtivos()).thenReturn(List.of());

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk());

        verify(service).listarAtivos();
    }

}