# Sistema de Pedidos 🚀

Projeto Spring Boot de alta performance para gerenciamento de pedidos, produtos e categorias. Desenvolvido com foco em **Java Moderno (21)**, imutabilidade e escalabilidade.

## 🛠️ Tecnologias e Diferenciais
* **Runtime:** **Java 21** (Utilizando **Virtual Threads** para processamento concorrente leve).
* **Framework:** **Spring Boot 3.4.x** (Uso de `@MockitoBean` para testes de integração).
* **Dados Imutáveis:** Implementação de **Records** para DTOs (Request/Response).
* **Persistência:** PostgreSQL (Dev/Prod) & H2 Database (Testes).
* **Mapeamento:** MapStruct & Lombok para código limpo e eficiente.
* **Documentação:** Swagger/OpenAPI 3 (SpringDoc) integrada.
* **Qualidade:** JaCoCo com cobertura de código (Meta: 100% em lógica de negócio).

---

## 📁 Estrutura do Projeto
O projeto segue uma arquitetura em camadas bem definida:
* `api`: Controllers, Records (DTOs), Exception Handlers (RFC 7807).
* `application`: Services (Regras de negócio), Mappers e validações.
* `domain`: Entidades JPA, Enums e Chaves Compostas.
* `infrastructure`: Repositórios, Configurações de Perfis (YAML) e Segurança.

---

## 📊 Qualidade de Código
Garantimos a integridade do sistema através de uma suíte de testes rigorosa:
* **Testes Unitários:** Validação isolada de Services e Mappers.
* **Testes de Integração:** Testes de ponta a ponta com banco de dados real (H2) e `MockMvc`.
* **Verificação Automatizada:** O build do Maven falha automaticamente se a cobertura do **JaCoCo** cair abaixo da meta estabelecida.

---

## 📦 Como rodar o projeto

### 1. Pré-requisitos
* **Docker & Docker Compose** instalados.
* **JDK 21** (OpenJDK ou GraalVM).
* **Maven 3.9+**.

### 2. Infraestrutura (Docker)
Inicie o banco de dados PostgreSQL:
```bash
docker-compose up -d
```

### 3. Build e execução
```bash
# Limpar, compilar e rodar testes com relatório de cobertura
mvn clean install

# Executar a aplicação
mvn spring-boot:run
```

---

## 🛠️ Manutenção da infraestrutura

### Como zerar o banco
```bash
# 1. Parar os containers e remover os volumes de dados persistidos
docker-compose down -v

# 2. Subir o banco novamente (o Hibernate recriará as tabelas do zero)
docker-compose up -d
```