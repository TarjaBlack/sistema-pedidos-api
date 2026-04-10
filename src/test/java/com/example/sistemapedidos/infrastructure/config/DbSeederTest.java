package com.example.sistemapedidos.infrastructure.config;

import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DbSeederTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private DbSeeder dbSeeder;

    @Test
    @DisplayName("Deve popular o banco quando estiver vazio (Caminho IF)")
    void devePopularBancoQuandoVazio() throws Exception {
        // Arrange: count retorna 0
        when(categoriaRepository.count()).thenReturn(0L);

        // Act
        dbSeeder.run();

        // Assert: Verifica se o saveAll foi chamado
        verify(categoriaRepository, times(1)).saveAll(anyList());
        verify(categoriaRepository, times(1)).count();
    }

    @Test
    @DisplayName("Não deve popular o banco quando já existirem dados (Caminho ELSE)")
    void naoDevePopularBancoQuandoJaExistemDados() throws Exception {
        // Arrange: count retorna 1 ou mais (Força a entrada no ELSE)
        when(categoriaRepository.count()).thenReturn(5L);

        // Act
        dbSeeder.run();

        // Assert: Garante que o saveAll NUNCA foi chamado
        verify(categoriaRepository, never()).saveAll(anyList());
        verify(categoriaRepository, times(1)).count();
    }
}