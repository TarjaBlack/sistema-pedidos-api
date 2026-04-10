package com.example.sistemapedidos.infrastructure.repositories;

import com.example.sistemapedidos.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
        List<Produto> findAllByAtivoTrue();

}
