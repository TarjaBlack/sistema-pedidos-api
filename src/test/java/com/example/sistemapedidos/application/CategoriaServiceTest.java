package com.example.sistemapedidos.application;

import com.example.sistemapedidos.api.dto.CategoriaDTO;
import com.example.sistemapedidos.api.dto.CategoriaRequestDTO;
import com.example.sistemapedidos.api.dto.CategoriaResponseDTO;
import com.example.sistemapedidos.application.common.FinderService;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        CategoriaRequestDTO request = new CategoriaRequestDTO("Informática");
        Categoria categoriaSalva = new Categoria(1L, "Informática", true);

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSalva);

        CategoriaResponseDTO resultado = categoriaService.salvar(request);

        assertNotNull(resultado);
        assertEquals(request.nome(),resultado.nome());
        assertEquals(1L, resultado.id());

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

    @Test
    @DisplayName("Deve desativar categoria (excluir lógico)")
    void deveExcluirLogico() {
        // Arrange
        Categoria categoria = new Categoria(1L, "Teste", true);
        when(finderService.categoriaOuFalhar(1L)).thenReturn(categoria);

        // Act
        categoriaService.excluirLogico(1L);

        // Assert
        assertFalse(categoria.isAtivo());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    @DisplayName("Deve listar apenas categorias ativas")
    void deveListarCategoriasAtivas() {
        // Arrange
        List<Categoria> listaSimulada = List.of(
                new Categoria(1L, "Eletrônicos", true),
                new Categoria(2L, "Livros", true)
        );
        when(categoriaRepository.findAllByAtivoTrue()).thenReturn(listaSimulada);

        // Act
        List<Categoria> resultado = categoriaService.listarAtivas();

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).isAtivo());
        verify(categoriaRepository, times(1)).findAllByAtivoTrue();
    }

    @Test
    @DisplayName("Deve desativar categoria com sucesso (Exclusão Lógica)")
    void deveExcluirLogicoComSucesso() {
        // Arrange
        Categoria categoriaViva = new Categoria(1L, "Roupas", true);
        when(finderService.categoriaOuFalhar(1L)).thenReturn(categoriaViva);

        // Act
        categoriaService.excluirLogico(1L);

        // Assert
        assertFalse(categoriaViva.isAtivo()); // Aqui está o pulo do gato para o 100%
        verify(categoriaRepository).save(categoriaViva);
    }

    @Test
    @DisplayName("Deve atualizar nome da categoria com sucesso")
    void deveAtualizarCategoria() {
        // Arrange
        Categoria categoriaAntiga = new Categoria(1L, "Nome Antigo", true);
        CategoriaDTO novoDado = new CategoriaDTO(null,"Nome Novo");

        when(finderService.categoriaOuFalhar(1L)).thenReturn(categoriaAntiga);
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Categoria resultado = categoriaService.atualizar(1L, novoDado);

        // Assert
        assertEquals("Nome Novo", resultado.getNome());
        verify(categoriaRepository).save(any(Categoria.class));
    }
}