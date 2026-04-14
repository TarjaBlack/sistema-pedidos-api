package com.example.sistemapedidos.application;

import com.example.sistemapedidos.api.dto.ProdutoRequestDTO;
import com.example.sistemapedidos.api.dto.ProdutoResponseDTO;
import com.example.sistemapedidos.application.common.FinderService;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.domain.exception.EntidadeNaoEncontradaException;
import com.example.sistemapedidos.infrastructure.repositories.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @Mock
    private FinderService finderService;

    @InjectMocks
    private ProdutoService service;

    // --- TESTES DE SUCESSO (HAPPY PATH) ---

    @Test
    @DisplayName("Deve salvar produto com sucesso")
    void deveSalvarProdutoComSucesso() {
        ProdutoRequestDTO request = new ProdutoRequestDTO("Teclado", BigDecimal.valueOf(200.0), 1L);
        Categoria categoria = new Categoria(1L, "Informática", true);
        Produto produtoSalvo = new Produto(10L, "Teclado", BigDecimal.valueOf(200.0), categoria, true);

        when(finderService.categoriaOuFalhar(1L)).thenReturn(categoria);
        when(repository.save(any(Produto.class))).thenReturn(produtoSalvo);

        ProdutoResponseDTO resultado = service.salvar(request);

        assertNotNull(resultado);
        assertEquals(10L, resultado.id());
        assertEquals("Teclado", resultado.nome());
        verify(repository).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() {
        // Arrange
        Long id = 1L;
        ProdutoRequestDTO dto = new ProdutoRequestDTO("Mouse Novo", BigDecimal.valueOf(150.0), 2L);
        Categoria novaCategoria = new Categoria(2L, "Periféricos", true);
        Produto produtoExistente = new Produto(id, "Mouse Antigo", BigDecimal.valueOf(100.0), new Categoria(), true);

        when(finderService.produtoOuFalhar(id)).thenReturn(produtoExistente);
        when(finderService.categoriaOuFalhar(2L)).thenReturn(novaCategoria);
        when(repository.save(any(Produto.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Produto resultado = service.atualizar(id, dto);

        // Assert
        assertEquals("Mouse Novo", resultado.getNome());
        assertEquals(novaCategoria, resultado.getCategoria());
        verify(repository).save(produtoExistente);
    }

    @Test
    @DisplayName("Deve realizar o delete lógico corretamente")
    void deveRealizarExcluirLogico() {
        // Arrange
        Long id = 1L;
        Produto produto = new Produto(id, "Cadeira", BigDecimal.valueOf(500), new Categoria(), true);
        when(finderService.produtoOuFalhar(id)).thenReturn(produto);

        // Act
        service.excluirLogico(id);

        // Assert
        assertFalse(produto.isAtivo());
        verify(repository).save(produto);
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorId() {
        Long id = 1L;
        Produto produto = new Produto(id, "Cadeira", BigDecimal.valueOf(500), new Categoria(), true);
        when(repository.findById(id)).thenReturn(Optional.of(produto));

        Produto resultado = service.buscarPorId(id);

        assertNotNull(resultado);
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve listar todos os produtos ativos")
    void deveListarApenasProdutosAtivos() {
        when(repository.findAllByAtivoTrue()).thenReturn(List.of(new Produto()));

        List<Produto> produtos = service.listarAtivos();

        assertFalse(produtos.isEmpty());
        verify(repository).findAllByAtivoTrue();
    }

    // --- TESTES DE EXCEÇÃO (EDGE CASES) ---

    @Test
    @DisplayName("Deve lançar exceção no buscarPorId quando não encontrar")
    void deveLancarExcecaoNoBuscarPorIdInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class, () -> service.buscarPorId(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção no salvar quando categoria não existe")
    void deveLancarExcecaoQuandoCategoriaInexistente() {
        ProdutoRequestDTO request = new ProdutoRequestDTO("Erro", BigDecimal.TEN, 99L);
        when(finderService.categoriaOuFalhar(99L)).thenThrow(new EntidadeNaoEncontradaException("Erro"));

        assertThrows(EntidadeNaoEncontradaException.class, () -> service.salvar(request));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar erro do repositório ao salvar")
    void deveLancarExcecaoQuandoErroNoRepositorio() {
        ProdutoRequestDTO request = new ProdutoRequestDTO("Erro", BigDecimal.TEN, 1L);
        when(finderService.categoriaOuFalhar(1L)).thenReturn(new Categoria());
        when(repository.save(any())).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> service.salvar(request));
    }
}