package com.example.sistemapedidos.api.mapper;

import com.example.sistemapedidos.api.dto.ProdutoResponseDTO;
import com.example.sistemapedidos.domain.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProdutoMapper {
    @Mapping(source="categoria.id", target="categoria.id")
    @Mapping(source="categoria.nome", target="categoria.nome")
    ProdutoResponseDTO toProdutoResponseDTO(Produto produto);
}
