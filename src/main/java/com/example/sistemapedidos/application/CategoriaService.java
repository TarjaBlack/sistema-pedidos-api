package com.example.sistemapedidos.application;

import com.example.sistemapedidos.api.dto.CategoriaDTO;
import com.example.sistemapedidos.api.dto.CategoriaRequestDTO;
import com.example.sistemapedidos.api.dto.CategoriaResponseDTO;
import com.example.sistemapedidos.api.mapper.CategoriaMapper;
import com.example.sistemapedidos.application.common.FinderService;
import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository repository;
    private final FinderService finderService;
    private final CategoriaMapper mapper;

    public CategoriaService(CategoriaRepository repository,
                            CategoriaMapper mapper,
                            FinderService finderService) {
        this.repository = repository;
        this.mapper = mapper;
        this.finderService = finderService;
    }

    @Transactional
    public CategoriaResponseDTO salvar(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        categoria.setAtivo(true);

        categoria = repository.save(categoria);

        return new CategoriaResponseDTO(categoria);
    }

    @Transactional
    public Categoria atualizar(Long id, CategoriaDTO dto) {
        Categoria categoria = finderService.categoriaOuFalhar(id);

        categoria.setNome(dto.nome());
        return repository.save(categoria);
    }

    @Transactional
    public void excluirLogico(Long id){
        Categoria categoria = finderService.categoriaOuFalhar(id);

        categoria.setAtivo(false);
        repository.save(categoria);
    }

    public List<Categoria> listarAtivas(){
        return  repository.findAllByAtivoTrue();
    }
}
