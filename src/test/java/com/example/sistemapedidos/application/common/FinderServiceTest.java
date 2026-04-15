package com.example.sistemapedidos.application.common;

import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.domain.Pedido;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.domain.exception.EntidadeNaoEncontradaException;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import com.example.sistemapedidos.infrastructure.repositories.PedidoRepository;
import com.example.sistemapedidos.infrastructure.repositories.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinderServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock 
    private ProdutoRepository produtoRepository;
    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private FinderService finderService;

    // --- Testes para Categoria ---

    @Test
    @DisplayName("Deve retornar categoria quando ID existe")
    void categoriaOuFalhar_deveRetornarCategoria_quandoEncontrar() {
        Categoria cat = new Categoria();
        cat.setId(1L);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));

        Categoria resultado = finderService.categoriaOuFalhar(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção para Categoria quando ID não existe")
    void categoriaOuFalhar_deveLancarExcecao_quandoNaoEncontrar() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class,
                () -> finderService.categoriaOuFalhar(99L));
    }

    // --- Testes para Produto ---

    @Test
    @DisplayName("Deve retornar produto quando ID existe")
    void produtoOuFalhar_deveRetornarProduto_quandoEncontrar() {
        Produto prod = new Produto();
        prod.setId(1L);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(prod));

        Produto resultado = finderService.produtoOuFalhar(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção para Produto quando ID não existe")
    void produtoOuFalhar_deveLancarExcecao_quandoNaoEncontrar() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class,
                () -> finderService.produtoOuFalhar(99L));
    }

    // --- Testes para Pedido ---

    @Test
    @DisplayName("Deve retornar pedido quando ID existe")
    void pedidoOuFalhar_deveRetornarPedido_quandoEncontrar() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Pedido resultado = finderService.pedidoOuFalhar(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção para Pedido quando ID não existe")
    void pedidoOuFalhar_deveLancarExcecao_quandoNaoEncontrar() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> finderService.pedidoOuFalhar(99L)
        );
        
        assertEquals("Pedido não encontrado", exception.getMessage());
    }
}