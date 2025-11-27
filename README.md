# **Agenda Fácil Pro API 🏥**

**Backend SaaS Multitenant** de alta performance projetado para clínicas e consultórios, focado em segurança ofensiva, escalabilidade e isolamento de dados.

## **🚀 Visão Geral da Arquitetura**

Este projeto segue os princípios da **Clean Architecture** (Arquitetura Limpa), isolando as regras de negócio (Core) de frameworks e bibliotecas (Infrastructure).

### **Destaques Técnicos**

* **Java 21 \+ Virtual Threads (Project Loom):** Habilitado nativamente para suportar milhares de requisições concorrentes (I/O Bound) com baixo consumo de memória.  
* **Segurança Avançada (The "Fortress"):**  
  * Autenticação via **JWT** com rotação de tokens (Access \+ Refresh).  
  * Armazenamento seguro em **Cookies HttpOnly** com SameSite=Strict (Blindado contra XSS e CSRF).  
  * **Rate Limiting** por IP utilizando Bucket4j \+ Caffeine Cache (Memória) para prevenção de DDoS/Brute-force.  
  * Hashing de senha com **BCrypt**.  
* **Multitenancy:** Isolamento lógico de dados por clínica utilizando TenantContext (ThreadLocal) e filtros de segurança.  
* **Performance:**  
  * **HikariCP:** Pool de conexões fixo otimizado para trabalhar em harmonia com Virtual Threads.  
  * **Redis Cache:** Estratégia *Cache-Aside* com serialização JSON customizada (Jackson MixIns) para entidades ricas.  
* **DevOps Ready:**  
  * **Docker Compose:** Ambiente local completo (API \+ Postgres \+ Redis) com redes isoladas e healthchecks.  
  * **Multi-Stage Build:** Dockerfile otimizado gerando imagens leves (Alpine JRE) e seguras (non-root user).  
* **Testes de Integração:** Uso de **Testcontainers** para subir bancos reais (Postgres/Redis) durante os testes, garantindo fidelidade total ao ambiente de produção.

## **🛠️ Tech Stack**

* **Linguagem:** Java 21  
* **Framework:** Spring Boot 3  
* **Banco de Dados:** PostgreSQL 16 (com Flyway Migrations)  
* **Cache:** Redis 7 (Alpine)  
* **ORM:** Hibernate / Spring Data JPA  
* **Segurança:** Spring Security 6, JJWT (io.jsonwebtoken)  
* **Rate Limit:** Bucket4j, Caffeine  
* **Testes:** JUnit 5, AssertJ, MockMvc, Testcontainers  
* **Doc:** SpringDoc OpenAPI (Swagger UI)

## **⚙️ Como Rodar (Docker \- Recomendado)**

A maneira mais fácil de iniciar a aplicação é usando o Docker Compose, que sobe o banco, o cache e a API simultaneamente.

1. **Clone o repositório:**  
   git clone \[https://github.com/seu-usuario/agenda-facil-pro.git\](https://github.com/seu-usuario/agenda-facil-pro.git)  
   cd agenda-facil-pro

2. Crie o arquivo de variáveis de ambiente (.env):  
   Crie um arquivo .env na raiz do projeto com o seguinte conteúdo (pode ajustar as senhas):

   ``` text
   PROJECT\_NAME=agenda-facil-pro  
   NETWORK\_NAME=agenda-network

   API\_PORT=8080  
   SPRING\_PROFILE=dev  
   JWT\_SECRET=SegredoSuperSecretoParaDesenvolvimentoLocal123456  
   COOKIE\_SECURE=false

   POSTGRES\_VERSION=16-alpine  
   POSTGRES\_DB=agenda\_db  
   POSTGRES\_USER=postgres  
   POSTGRES\_PASSWORD=password  
   POSTGRES\_PORT=5432

   REDIS\_VERSION=7-alpine  
   REDIS\_PORT=6379

4. **Suba os containers:**  
   docker-compose up \-d \--build

5. Acesse a Documentação (Swagger):  
   Após a aplicação iniciar (aguarde log "Started AgendaFacilProApplication"), acesse:  
   👉 http://localhost:8080/swagger-ui/index.html

## **🧪 Como Rodar Testes**

O projeto utiliza **Testcontainers**. Você precisa ter o Docker rodando na sua máquina para executar os testes.

mvn test

* **Nota:** Os testes de integração sobem containers efêmeros de Postgres e Redis. Se o Docker não estiver rodando, os testes falharão.  
* Os testes validam: Fluxo de Login, Geração de Cookies, Bloqueio de Rate Limit (429), Unicidade de CNPJ no banco e Consultas Nativas de Permissões.

## **🔒 Detalhes de Segurança**

### **Fluxo de Autenticação (Cookie-Based)**

Diferente de APIs tradicionais que retornam o Token no JSON, esta API utiliza uma abordagem *Full-Cookie* para proteger o cliente Web.

1. **Login (POST /api/auth/login):** Recebe credenciais.  
2. **Resposta:** Retorna HTTP 200 com JSON contendo dados do usuário (Nome, Email), mas **SEM tokens**.  
3. **Cookies:** O servidor "seta" dois cookies na resposta:  
   * access\_token: Curta duração (15 min). HttpOnly, Secure, SameSite=Strict.  
   * refresh\_token: Longa duração (7 dias). HttpOnly, Secure, SameSite=Strict.  
4. **Requisições Seguras:** O Frontend não precisa enviar header Authorization. O navegador envia os cookies automaticamente. O Backend intercepta, valida e define o Tenant.

### **Rate Limiter**

Para proteger contra abuso, implementamos um filtro de balde de tokens (Token Bucket).

* **Regra Padrão:** 100 requisições por minuto **por IP**.  
* **Armazenamento:** Em memória (Caffeine) com expiração automática.  
* **Excesso:** Retorna HTTP 429 com JSON padronizado ErrorResponse.

## **📂 Estrutura de Pastas (Clean Arch)**

src/main/java/api/agendafacilpro  
├── core                    \# 🧠 REGRAS DE NEGÓCIO (Puro Java, sem Frameworks)  
│   ├── domain              \# Entidades, Value Objects, Enums  
│   ├── gateway             \# Interfaces (Portas de Saída)  
│   ├── usecases            \# Lógica de Aplicação (Inputs, Outputs, Interactors)  
│   └── exceptions          \# Exceções de Domínio  
│  
└── infraestructure         \# 🔌 ADAPTADORES & FRAMEWORKS (Spring, Hibernate, etc)  
    ├── config              \# Configurações (Security, Swagger, Redis, Jackson)  
    ├── gateway             \# Implementação das interfaces do Core  
    ├── persistence         \# Entidades JPA e Repositórios  
    ├── web                 \# Controllers, DTOs, ExceptionHandlers, Filters  
    └── service             \# Serviços de Infra (JwtService, etc)

## **📝 Perfis de Execução**

O comportamento da aplicação muda drasticamente entre dev e prod através do application.yml.

| Recurso | Perfil dev | Perfil prod |
| :---- | :---- | :---- |
| **Logs** | DEBUG (SQL formatado e colorido) | INFO (JSON ou compacto) |
| **Cookies** | Secure=false (HTTP) | Secure=true (HTTPS Obrigatório) |
| **Pool DB** | Elástico (Min 5, Max 10\) | Fixo (Min 20, Max 20\) |
| **Hibernate** | format\_sql=true | format\_sql=false |
| **Swagger** | Habilitado | Habilitado (Pode ser desligado) |
