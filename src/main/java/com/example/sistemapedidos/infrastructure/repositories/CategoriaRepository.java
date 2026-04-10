package com.example.sistemapedidos.infrastructure.repositories;

import com.example.sistemapedidos.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Long> {
    List<Categoria> findAllByAtivoTrue();
}
