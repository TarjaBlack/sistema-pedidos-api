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
    public void run(String... args) throws Exception {
        // Verifica se a tabela está vazia antes de fazer qualquer coisa
        if (categoriaRepository.count() == 0) {
            Categoria c1 = new Categoria(null, "Eletronicos");
            Categoria c2 = new Categoria(null, "Perifericos");

            categoriaRepository.saveAll(List.of(c1, c2));

            System.out.println("🌱 Banco de dados estava vazio. Categorias populadas com sucesso!!!");
        } else {
            System.out.println("ℹ️ Categorias já existentes no banco. Pulando a semeadura.");
        }
    }
}