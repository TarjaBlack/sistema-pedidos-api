package com.example.sistemapedidos.application;

import com.example.sistemapedidos.api.dto.ProdutoRequestDTO;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import com.example.sistemapedidos.infrastructure.repositories.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProdutoService service;

    @Test
    void deveLançarExcecaoQuandoErroNoRepositorio(){
        ProdutoRequestDTO request = new ProdutoRequestDTO("Erro", BigDecimal.valueOf(10.0), 1L);
        Categoria categoria = new Categoria(1L, "Eletronicos");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(repository.save(any(Produto.class))).thenThrow(new RuntimeException("Erro de conexão com o banco"));

        assertThrows(RuntimeException.class, () -> {
            service.salvar(request);
        });

        verify(repository, times(1)).save(any(Produto.class));

    }

    @Test
    void deveSalvarProdutoComSucesso() {
        // 1. Arrange
        ProdutoRequestDTO request = new ProdutoRequestDTO("Teclado", BigDecimal.valueOf(200.0), 1L);
        Categoria categoria = new Categoria(1L, "Informática");
        // Ajustei para BigDecimal se o seu modelo usar esse tipo, ou mantenha Double se for o caso
        Produto produtoSalvo = new Produto(10L, "Teclado", BigDecimal.valueOf(200.0), categoria);

        // Precisamos configurar os DOIS mocks aqui
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.of(categoria));
        when(repository.save(any(Produto.class))).thenReturn(produtoSalvo);

        // 2. Act
        Produto resultado = service.salvar(request);

        // 3. Assert
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId()); // Comparando ID com ID
        assertEquals("Teclado", resultado.getNome());

        verify(categoriaRepository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Produto.class));
    }

}