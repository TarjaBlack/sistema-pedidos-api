package com.example.sistemapedidos.application.common;

import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.domain.Pedido;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.domain.exception.EntidadeNaoEncontradaException;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import com.example.sistemapedidos.infrastructure.repositories.PedidoRepository;
import com.example.sistemapedidos.infrastructure.repositories.ProdutoRepository;
import org.springframework.stereotype.Component;

@Component
public class FinderService {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public FinderService(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Categoria categoriaOuFalhar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Categoria não encontrada"));
    }

    public Produto produtoOuFalhar(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado"));
    }

    public Pedido pedidoOuFalhar(Long id){
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Pedido não encontrado"));
    }
}