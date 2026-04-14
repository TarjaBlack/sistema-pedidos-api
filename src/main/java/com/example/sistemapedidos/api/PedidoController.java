package com.example.sistemapedidos.api;


import com.example.sistemapedidos.api.dto.PedidoRequestDTO;
import com.example.sistemapedidos.api.dto.PedidoResponseDTO;
import com.example.sistemapedidos.api.dto.StatusUpdateDTO;
import com.example.sistemapedidos.application.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/pedidos")
@RequiredArgsConstructor // Substitui o @Autowired (Injeção via construtor do Lombok)
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Cria um novo pedido", description = "Persiste um pedido no banco e retorna o DTO de resposta")
    @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso")
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> inserir(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO novoPedido = pedidoService.inserir(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoPedido.id()).toUri();

        return ResponseEntity.created(uri).body(novoPedido);
    }

    @Operation(summary = "Atualiza o status de um pedido", description = "Altera o status do pedido validando a transição de estados")
    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateDTO dto) {

        PedidoResponseDTO response = pedidoService.atualizarStatus(id, dto.status());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca um pedido por ID", description = "Retorna os detalhes do pedido e seus itens")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        // Aproveitando o seu novo padrão de retorno
        PedidoResponseDTO response = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }
}