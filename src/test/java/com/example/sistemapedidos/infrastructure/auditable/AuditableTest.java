package com.example.sistemapedidos.infrastructure.auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuditableTest {

    // Entidade dummy (fictícia) apenas para podermos testar a classe abstrata Auditable
    @Entity
    static class DummyAuditableEntity extends Auditable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        private String nome;

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

    @Test
    void settersEGetters_DevemFuncionarCorretamente() {
        // Arrange
        DummyAuditableEntity entity = new DummyAuditableEntity();
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime depois = agora.plusDays(1);

        // Act
        entity.setDataCriacao(agora);
        entity.setDataAtualizacao(depois);

        // Assert
        assertNotNull(entity.getDataCriacao());
        assertNotNull(entity.getDataAtualizacao());
        assertEquals(agora, entity.getDataCriacao());
        assertEquals(depois, entity.getDataAtualizacao());
    }
}
