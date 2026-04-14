package com.example.sistemapedidos.application;

import com.example.sistemapedidos.api.dto.ProdutoRequestDTO;
import com.example.sistemapedidos.api.dto.ProdutoResponseDTO;
import com.example.sistemapedidos.application.common.FinderService;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.domain.Produto;
import com.example.sistemapedidos.domain.exception.EntidadeNaoEncontradaException;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import com.example.sistemapedidos.infrastructure.repositories.ProdutoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final FinderService finderService;

    public ProdutoService(ProdutoRepository produtoRepository,  FinderService finderService) {
        this.produtoRepository = produtoRepository;
        this.finderService = finderService;
    }

    public Produto atualizar(Long id, @Valid ProdutoRequestDTO produtoRequestDTO) {
        Produto produto = finderService.produtoOuFalhar(id);

        Categoria categoria = finderService.categoriaOuFalhar(produtoRequestDTO.getCategoriaId());

        produto.setNome(produtoRequestDTO.getNome());
        produto.setPreco(produtoRequestDTO.getPreco());
        produto.setCategoria(categoria);

        return produtoRepository.save(produto);
    }

    @Transactional
    public void excluirLogico(Long id){
        Produto produto = finderService.produtoOuFalhar(id);

        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    @Transactional
    public ProdutoResponseDTO salvar(ProdutoRequestDTO dto){
        Categoria categoria = finderService.categoriaOuFalhar(dto.getCategoriaId());

        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(categoria);
        produto.setAtivo(true);

        produto = produtoRepository.save(produto);


        return new ProdutoResponseDTO(produto);
    }

    public List<Produto> listarAtivos(){
        return produtoRepository.findAllByAtivoTrue();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(()->new EntidadeNaoEncontradaException("Produto não encontrado com o ID" + id));
    }
}
