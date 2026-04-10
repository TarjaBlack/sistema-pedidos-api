# Sistema de Pedidos 🚀

Projeto Spring Boot de alta performance para gerenciamento de produtos e categorias, focado em qualidade de código, testes automatizados e integração contínua (CI/CD).

## 🛠️ Tecnologias e Diferenciais
* **Runtime:** [GraalVM 17](https://www.graalvm.org/) (Otimizado para performance)
* **Framework:** Spring Boot 3.4.3
* **Persistência:** PostgreSQL (Produção/Dev) & H2 Database (Testes)
* **Mapeamento:** MapStruct & Lombok
* **Documentação:** Swagger/OpenAPI 3 (SpringDoc 2.8.5)
* **Qualidade:** JaCoCo (Cobertura de código 100% em lógica de negócio)
* **CI/CD:** GitHub Actions (Pipeline automatizado de build e teste)

## 📁 Estrutura do Projeto
O projeto segue uma arquitetura em camadas para facilitar a manutenção e escalabilidade:
* `api`: Controllers, DTOs e Exception Handlers.
* `application`: Serviços e lógica de negócio.
* `domain`: Entidades JPA e exceções de domínio.
* `infrastructure`: Repositórios, Configurações e Seeders de banco.



---

## 📊 Qualidade do Código
O projeto mantém uma rigorosa suíte de testes. O pipeline do **GitHub Actions** valida automaticamente a cada *push* ou *pull request*:
* Compilação e Build
* Execução de Testes Unitários e de Integração
* Verificação de cobertura mínima via JaCoCo (Build falha se abaixo da meta definida)

---

## 📦 Como rodar o projeto

### 1. Pré-requisitos
* **Docker & Docker Compose** instalado.
* **JDK 17** (Preferencialmente GraalVM).
* **Maven 3.9+**.

### 2. Variáveis de Ambiente
O projeto utiliza perfis do Spring. Para produção/dev com Docker, as principais variáveis no `application.properties` são:
* `SPRING_DATASOURCE_URL`: jdbc:postgresql://localhost:5432/pedidos
* `SPRING_DATASOURCE_USERNAME`: postgres
* `SPRING_DATASOURCE_PASSWORD`: postgres

### 3. Infraestrutura (Docker)
Inicie o banco de dados PostgreSQL:
```bash
docker-compose up -d