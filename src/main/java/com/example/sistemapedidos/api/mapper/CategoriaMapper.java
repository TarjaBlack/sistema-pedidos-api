package com.example.sistemapedidos.api.mapper;

import com.example.sistemapedidos.api.dto.CategoriaDTO;
import com.example.sistemapedidos.domain.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    CategoriaDTO toDTO(Categoria categoria);
}
