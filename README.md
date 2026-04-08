# Sistema de Pedidos 🚀

Projeto Spring Boot para gerenciamento de produtos e categorias, utilizando boas práticas de desenvolvimento, documentação com Swagger e persistência em PostgreSQL via Docker.

## 🛠️ Tecnologias Utilizadas
* **Java 21**
* **Spring Boot 4.0.5**
* **Spring Data JPA**
* **PostgreSQL** (Banco de dados de produção/dev)
* **H2 Database** (Banco de dados de testes em memória)
* **Docker & Docker Compose**
* **Swagger/OpenAPI 2.8.5**

---

## 📦 Como rodar o projeto

### 1. Pré-requisitos
* Docker Desktop instalado e rodando.
* JDK 21+.
* Maven 3.x.

### 2. Subindo o Banco de Dados (Docker)
Na raiz do projeto (onde está o arquivo `docker-compose.yml`), execute o comando abaixo no terminal:

```bash
docker-compose up -d