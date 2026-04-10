package com.example.sistemapedidos.application;

import com.example.sistemapedidos.api.dto.CategoriaDTO;
import com.example.sistemapedidos.application.common.FinderService;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private FinderService finderService;

    @InjectMocks
    private CategoriaService categoriaService;

    private CategoriaDTO categoriaDTO;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoriaDTO = new CategoriaDTO(1L, "Eletrônicos");
        
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Eletrônicos");
        categoria.setAtivo(true);
    }

    @Test
    void salvar_DeveSalvarERetornarCategoria() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria resultado = categoriaService.salvar(categoriaDTO);

        assertNotNull(resultado);
        assertEquals(categoriaDTO.nome(), resultado.getNome());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void atualizar_DeveAtualizarERetornarCategoria_QuandoCategoriaExiste() {
        when(finderService.categoriaOuFalhar(1L)).thenReturn(categoria);

        CategoriaDTO dtoAtualizacao = new CategoriaDTO(1L, "Móveis");
        
        // Simular a mudança que o service vai fazer e o save vai retornar
        Categoria categoriaAtualizada = new Categoria();
        categoriaAtualizada.setId(1L);
        categoriaAtualizada.setNome("Móveis");
        categoriaAtualizada.setAtivo(true);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaAtualizada);

        Categoria resultado = categoriaService.atualizar(1L, dtoAtualizacao);

        assertNotNull(resultado);
        assertEquals("Móveis", resultado.getNome());
        verify(finderService, times(1)).categoriaOuFalhar(1L);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void excluirLogico_DeveDesativarCategoria_QuandoCategoriaExiste() {
        when(finderService.categoriaOuFalhar(1L)).thenReturn(categoria);

        categoriaService.excluirLogico(1L);

        assertFalse(categoria.isAtivo());
        verify(finderService, times(1)).categoriaOuFalhar(1L);
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void listarAtivas_DeveRetornarListaDeCategoriasAtivas() {
        when(categoriaRepository.findAllByAtivoTrue()).thenReturn(List.of(categoria));

        List<Categoria> resultado = categoriaService.listarAtivas();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(categoriaRepository, times(1)).findAllByAtivoTrue();
    }
}