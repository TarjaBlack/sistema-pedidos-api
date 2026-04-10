package com.example.sistemapedidos.application.common;

import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.domain.exception.EntidadeNaoEncontradaException;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
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
    @Mock private ProdutoRepository produtoRepository;
    @InjectMocks
    private FinderService finderService;

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoExiste() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class,
                () -> finderService.categoriaOuFalhar(99L));
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class,
                () -> finderService.produtoOuFalhar(99L));
    }

    @Test
    @DisplayName("Deve retornar categoria quando encontrar")
    void deveRetornarCategoriaQuandoSucesso() {
        Categoria cat = new Categoria();
        cat.setId(1L);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));

        Categoria resultado = finderService.categoriaOuFalhar(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Deve retornar produto quando encontrar")
    void deveRetornarProdutoQuandoSucesso() {
        Produto prod = new Produto();
        prod.setId(1L);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(prod));

        Produto resultado = finderService.produtoOuFalhar(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }
}