package com.example.sistemapedidos.api;

import com.example.sistemapedidos.api.dto.ProdutoRequestDTO;
import com.example.sistemapedidos.api.dto.ProdutoResponseDTO;
import com.example.sistemapedidos.api.mapper.ProdutoMapper;
import com.example.sistemapedidos.application.ProdutoService;
import com.example.sistemapedidos.domain.Produto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @Operation(summary = "Cria um novo produto", description = "Persiste um produto no banco e retorna o DTO de resposta")
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso")
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO dto) {
        // O service já retorna o DTO ou a Entidade convertida
        ProdutoResponseDTO response = service.salvar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id()) // Supondo que o Record tem o campo id
                .toUri();

        return ResponseEntity.created(uri).body(response);
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
