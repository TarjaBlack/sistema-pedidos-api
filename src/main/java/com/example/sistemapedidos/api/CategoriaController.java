package com.example.sistemapedidos.api;

import com.example.sistemapedidos.api.dto.CategoriaDTO;
import com.example.sistemapedidos.api.dto.CategoriaRequestDTO;
import com.example.sistemapedidos.api.dto.CategoriaResponseDTO;
import com.example.sistemapedidos.application.CategoriaService;
import com.example.sistemapedidos.domain.Categoria;
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
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService){
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<Categoria> listar(){
        return categoriaService.listarAtivas();
    }

    @Operation(summary = "Cria uma nova catgoria", description = "Persiste uma categoria no banco e retorna o DTO de resposta")
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso")
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(@Valid @RequestBody CategoriaRequestDTO dto) {
        CategoriaResponseDTO response = categoriaService.salvar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }
    @PutMapping("/{id}")
    public Categoria atualizar(@PathVariable Long id, @RequestBody CategoriaDTO dto){
        return  categoriaService.atualizar(id,dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        categoriaService.excluirLogico(id);
    }
}
