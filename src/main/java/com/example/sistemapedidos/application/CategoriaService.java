package com.example.sistemapedidos.application;

import com.example.sistemapedidos.api.dto.CategoriaDTO;
import com.example.sistemapedidos.application.common.FinderService;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final FinderService finderService;


    public CategoriaService(CategoriaRepository categoriaRepository, FinderService finderService) {
        this.categoriaRepository = categoriaRepository;
        this.finderService = finderService;
    }

    @Transactional
    public Categoria salvar(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria atualizar(Long id, CategoriaDTO dto) {
        Categoria categoria = finderService.categoriaOuFalhar(id);

        categoria.setNome(dto.nome());
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void excluirLogico(Long id){
        Categoria categoria = finderService.categoriaOuFalhar(id);

        categoria.setAtivo(false);
        categoriaRepository.save(categoria);
    }

    public List<Categoria> listarAtivas(){
        return  categoriaRepository.findAllByAtivoTrue();
    }
}
