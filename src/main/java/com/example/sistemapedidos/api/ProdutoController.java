package com.example.sistemapedidos.api;

import com.example.sistemapedidos.api.dto.ProdutoRequestDTO;
import com.example.sistemapedidos.api.dto.ProdutoResponseDTO;
import com.example.sistemapedidos.api.mapper.ProdutoMapper;
import com.example.sistemapedidos.application.ProdutoService;
import com.example.sistemapedidos.domain.Produto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO dto){
        Produto produto = service.atualizar(id, dto);
        return ResponseEntity.ok(produtoMapper.toProdutoResponseDTO(produto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        service.excluirLogico(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponseDTO criar(@Valid @RequestBody ProdutoRequestDTO dto){
        Produto produtoSalvo = service.salvar(dto);

        return produtoMapper.toProdutoResponseDTO(produtoSalvo);
    }

    @GetMapping
    public List<ProdutoResponseDTO> listar(){
        return service.listarAtivos().stream()
                .map(produtoMapper::toProdutoResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id){

        Produto produto = service.buscarPorId(id);

        return ResponseEntity.ok(produtoMapper.toProdutoResponseDTO(produto));
    }
}
