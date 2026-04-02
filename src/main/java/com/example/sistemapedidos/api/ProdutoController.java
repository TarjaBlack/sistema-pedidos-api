package com.example.sistemapedidos.api;

import com.example.sistemapedidos.api.dto.CategoriaDTO;
import com.example.sistemapedidos.api.dto.ProdutoRequestDTO;
import com.example.sistemapedidos.api.dto.ProdutoResponseDTO;
import com.example.sistemapedidos.api.mapper.ProdutoMapper;
import com.example.sistemapedidos.application.ProdutoService;
import com.example.sistemapedidos.domain.Produto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService service;
    private final ProdutoMapper produtoMapper;

    public ProdutoController(ProdutoService service, ProdutoMapper produtoMapper) {
        this.service = service;
        this.produtoMapper = produtoMapper;
    }

    @PostMapping
    public ProdutoResponseDTO criar(@Valid @RequestBody ProdutoRequestDTO dto){
        Produto produtoSalvo = service.salvar(dto);

        return produtoMapper.toProdutoResponseDTO(produtoSalvo);
    }

    @GetMapping
    public List<ProdutoResponseDTO> listar(){
        return service.listarTodos().stream()
                .map(produtoMapper::toProdutoResponseDTO)
                .toList();
    }
}
