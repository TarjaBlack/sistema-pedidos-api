package com.example.sistemapedidos.application;

import com.example.sistemapedidos.api.dto.ProdutoRequestDTO;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.domain.exception.EntidadeNaoEncontradaException;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import com.example.sistemapedidos.infrastructure.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Produto salvar(ProdutoRequestDTO dto){
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(()-> new EntidadeNaoEncontradaException("Categoria não encontrada"));

        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(categoria);


        return produtoRepository.save(produto);
    }

    public List<Produto> listarTodos(){
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(()->new EntidadeNaoEncontradaException("Produto não encontrado com o ID" + id));
    }
}
