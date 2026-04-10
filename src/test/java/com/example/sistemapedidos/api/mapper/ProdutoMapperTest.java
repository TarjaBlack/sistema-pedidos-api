package com.example.sistemapedidos.api.mapper;

import com.example.sistemapedidos.api.dto.ProdutoResponseDTO;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.domain.Produto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoMapperTest {
    private final ProdutoMapper produtoMapper = Mappers.getMapper(ProdutoMapper.class);

    @Test
    void deveMapearProdutoParaProdutoResponseDTO(){
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Eletrônicos");


        Produto produto = new Produto();
        produto.setId(100L);
        produto.setNome("Mouse Gamer");
        produto.setPreco(BigDecimal.valueOf(150));
        produto.setCategoria(categoria);

        ProdutoResponseDTO dto = produtoMapper.toProdutoResponseDTO(produto);

        assertNotNull(dto);
        assertEquals(produto.getId(), dto.getId());
        assertEquals(produto.getNome(), dto.getNome());
        assertEquals(produto.getPreco(), dto.getPreco());
    }

}