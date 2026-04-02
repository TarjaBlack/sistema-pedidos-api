package com.example.sistemapedidos.infrastructure.config;

import com.example.sistemapedidos.domain.Categoria;
import com.example.sistemapedidos.infrastructure.repositories.CategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DbSeeder implements CommandLineRunner {
    private final CategoriaRepository categoriaRepository;

    public DbSeeder(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public void run(String... args) throws Exception{
        categoriaRepository.deleteAll();

        Categoria c1 = new Categoria(null,"Eletronicos");
        Categoria c2 = new Categoria(null, "Perifericos");

        categoriaRepository.saveAll(List.of(c1,c2));

        System.out.println("Categorias populadas com sucesso!!!");
    }
}
