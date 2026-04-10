package com.example.sistemapedidos.application;

import com.example.sistemapedidos.api.dto.ProdutoRequestDTO;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import com.example.sistemapedidos.infrastructure.repositories.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Carrega o contexto completo do Spring
@ActiveProfiles("test") // Ativa o H2 definido no seu application.yml
@Transactional // Faz rollback após cada teste para manter o banco limpo
class ProdutoServiceIT {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaSalva;

    @BeforeEach
    void setup() {
        // Preparamos o banco H2 com uma categoria real para os testes
        Categoria categoria = new Categoria();
        categoria.setNome("Hardware");
        categoria.setAtivo(true);
        categoriaSalva = categoriaRepository.save(categoria);
    }

    @Test
    @DisplayName("Deve persistir um produto no banco H2 real")
    void deveSalvarNoBancoReal() {
        // Arrange
        ProdutoRequestDTO dto = new ProdutoRequestDTO("Placa de Vídeo", new BigDecimal("3500.00"), categoriaSalva.getId());

        // Act
        Produto resultado = produtoService.salvar(dto);

        // Assert
        assertNotNull(resultado.getId());
        assertEquals("Placa de Vídeo", resultado.getNome());

        // Verificação extra: o dado realmente existe no banco?
        assertTrue(produtoRepository.findById(resultado.getId()).isPresent());
    }

    @Test
    @DisplayName("Deve refletir o delete lógico no banco de dados")
    void deveRefletirDeleteLogicoNoBanco() {
        // Arrange
        Produto p = new Produto(null, "SSD", BigDecimal.TEN, categoriaSalva, true);
        p = produtoRepository.save(p);

        // Act
        produtoService.excluirLogico(p.getId());

        // Assert
        Produto produtoNoBanco = produtoRepository.findById(p.getId()).get();
        assertFalse(produtoNoBanco.isAtivo(), "O campo ativo deve ser false no banco");
    }

    @Test
    @DisplayName("Deve filtrar produtos ativos via consulta JPA real")
    void deveFiltrarAtivosNoBanco() {
        // Arrange
        Produto p1 = produtoRepository.save(new Produto(null, "Ativo", BigDecimal.ONE, categoriaSalva, true));
        Produto p2 = produtoRepository.save(new Produto(null, "Inativo", BigDecimal.ONE, categoriaSalva, false));

        // Act
        List<Produto> ativos = produtoService.listarAtivos();

        // Assert
        assertTrue(ativos.stream().anyMatch(p -> p.getNome().equals("Ativo")));
        assertTrue(ativos.stream().noneMatch(p -> p.getNome().equals("Inativo")));
    }
}