# Consultório Médico API

Uma API robusta e escalável desenvolvida com Spring Boot para gerenciamento completo de um consultório médico, incluindo pacientes, médicos, recepcionistas, consultas e históricos médicos.

---

## 🧪 Testes Unitários

### ⚠️ Status Atual: Iniciando Implementação

A API está em fase de **implementação inicial de testes unitários**. Estamos utilizando:

- **JUnit 5** - Framework de testes
- **Mockito** - Framework para criar mocks e verificar comportamentos
- **Spring Boot Test** - Utilities para testes Spring

### Estrutura de Testes

Os testes estão organizados em: `src/test/java/br/com/rodrigo/consultorio_medico_api/`

```
src/test/
└── java/br/com/rodrigo/consultorio_medico_api/
    ├── ConsultorioMedicoApiApplicationTests.java
    └── service/
        └── PacienteServiceTest.java
```

### Testes Existentes

#### 1. Teste de Contexto (ConsultorioMedicoApiApplicationTests.java)

```java
@SpringBootTest
class ConsultorioMedicoApiApplicationTests {

    @Test
    void contextLoads() {
        // Valida que a aplicação foi carregada corretamente
    }
}
```

#### 2. Testes de Serviço (PacienteServiceTest.java)

Inicialmente implementando testes para o PacienteService

### Executando Testes

#### Rodar Todos os Testes

```bash
# Com Maven
mvn test

# Com Maven (verbose)
mvn test -X

# Com Maven (arquivo específico)
mvn test -Dtest=PacienteServiceTest
```

#### Rodar Testes com Cobertura

```bash
# Adicionar dependência Maven Surefire (já incluída no pom.xml)
mvn test jacoco:report

# Visualizar relatório
open target/site/jacoco/index.html
```

### Estrutura de um Teste com Mockito

Exemplo padrão para testes com Mockito:

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSavePaciente() {
        // Arrange
        PacienteRequest request = new PacienteRequest(...);
        Paciente paciente = new Paciente(...);
        
        when(pacienteRepository.save(any())).thenReturn(paciente);

        // Act
        PacienteResponse response = pacienteService.save(request);

        // Assert
        assertNotNull(response);
        verify(pacienteRepository, times(1)).save(any());
    }
}
```


## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Configuração](#instalação-e-configuração)
- [Executando a Aplicação](#executando-a-aplicação)
- [Autenticação e Autorização](#autenticação-e-autorização)
- [Endpoints da API](#endpoints-da-api)
  - [Pacientes](#pacientes)
  - [Médicos](#médicos)
  - [Recepcionistas](#recepcionistas)
  - [Consultas](#consultas)
  - [Histórico Médico](#histórico-médico)
- [Exemplos de Requisições](#exemplos-de-requisições)
- [Notas Importantes](#notas-importantes)
- [Contribuindo](#contribuindo)

---

## 🎯 Sobre o Projeto

A **Consultório Médico API** é um sistema completo de gerenciamento para consultórios médicos, permitindo:

- ✅ Cadastro e gerenciamento de pacientes
- ✅ Cadastro e gerenciamento de médicos com especialidades
- ✅ Gerenciamento de recepcionistas
- ✅ Agendamento e controle de consultas
- ✅ Histórico médico de pacientes
- ✅ Registro de exames médicos
- ✅ Autenticação segura com HTTP Basic Auth
- ✅ Controle de acesso baseado em papéis (RBAC)

O projeto está em fase de desenvolvimento com foco em implementação de testes unitários robustos.

---

## 🛠 Tecnologias Utilizadas

### Backend
- **Java 21** - Linguagem de programação
- **Spring Boot 4.1.0** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Autenticação e autorização
- **Spring Validation** - Validação de dados
- **MapStruct 1.5.5** - Mapeamento de DTOs para Entities
- **Lombok** - Redução de boilerplate

### Database
- **PostgreSQL 17** - Banco de dados relacional
- **Hibernate** - ORM (Object-Relational Mapping)

### Build & Testing
- **Maven 3.9.6** - Gerenciador de dependências
- **JUnit 5** - Framework de testes unitários
- **Mockito** - Mock framework (iniciando implementação)
- **Spring Security Test** - Testes de segurança

### Containerização
- **Docker** - Containerização da aplicação
- **Docker Compose** - Orquestração de containers

---

## 📁 Estrutura do Projeto

```
consultorio-medico-api/
├── src/
│   ├── main/
│   │   ├── java/br/com/rodrigo/consultorio_medico_api/
│   │   │   ├── ConsultorioMedicoApiApplication.java    # Classe principal
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java                  # Configuração de segurança
│   │   │   ├── controller/                              # Controladores REST
│   │   │   │   ├── PacienteController.java
│   │   │   │   ├── MedicoController.java
│   │   │   │   ├── RecepcionistaController.java
│   │   │   │   ├── ConsultaController.java
│   │   │   │   └── HistoricoMedicoController.java
│   │   │   ├── service/                                 # Lógica de negócio
│   │   │   ├── repository/                              # Acesso a dados
│   │   │   ├── entity/                                  # Entidades JPA
│   │   │   ├── dto/                                     # Data Transfer Objects
│   │   │   │   ├── paciente/
│   │   │   │   ├── medico/
│   │   │   │   ├── recepcionista/
│   │   │   │   ├── consulta/
│   │   │   │   ├── exame_medico/
│   │   │   │   ├── historico_medico/
│   │   │   │   └── usuario/
│   │   │   ├── mapper/                                  # Mapeadores MapStruct
│   │   │   └── execption/                               # Exceções customizadas
│   │   └── resources/
│   │       └── application.yml                          # Configurações da aplicação
│   └── test/
│       └── java/br/com/rodrigo/consultorio_medico_api/
│           └── service/                                 # Testes de serviço
├── dockerfile
├── compose.yml
├── pom.xml
└── README.md
```

---

## 📦 Pré-requisitos

Antes de começar, você precisará ter instalado:

- **Java 21** ou superior
- **Maven 3.9.6** ou superior
- **Docker** e **Docker Compose** (para execução containerizada)
- **PostgreSQL 17** (se executar sem Docker)
- **Git** (para clonar o repositório)

### Verificar instalações

```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar Docker
docker --version
docker-compose --version
```

---

## 🔧 Instalação e Configuração

### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/consultorio-medico-api.git
cd consultorio-medico-api
```

### 2. Configurar Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto com as variáveis necessárias:

```env
POSTGRES_DB=consultorio_medico
POSTGRES_USER=consultorio_user
POSTGRES_PASSWORD=sua_senha_segura_aqui
```

### 3. Estrutura do `application.yml`

O arquivo `src/main/resources/application.yml` já está configurado para usar as variáveis de ambiente:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/consultorio_medico
    username: ${POSTGRES_USER}
    password: ${POSTGRES_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## ▶️ Executando a Aplicação

### Opção 1: Com Docker Compose (Recomendado)

```bash
# Construir e iniciar os containers
docker-compose up -d

# Verificar os logs
docker-compose logs -f app

# Parar os containers
docker-compose down
```

A API estará disponível em: `http://localhost:8080`

### Opção 2: Localmente com Maven

#### Pré-requisitos Adicionais:
- PostgreSQL rodando localmente na porta 5432

#### Passos:

```bash
# 1. Instalar dependências
mvn clean install

# 2. Executar a aplicação
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

---

## 🔐 Autenticação e Autorização

A API utiliza **HTTP Basic Authentication** com controle de acesso baseado em papéis (RBAC).

### Papéis Disponíveis

- **ROLE_ADMIN** - Acesso total ao sistema
- **ROLE_RECEPCIONISTA** - Gerenciamento de pacientes e consultas
- **ROLE_MEDICO** - Acesso a histórico médico e exames
- **ROLE_PACIENTE** - Acesso aos próprios dados

### Autenticação HTTP Basic

Use as credenciais para autenticar:

```bash
# Exemplo com curl
curl -u email@example.com:senha http://localhost:8080/api/paciente/busca/all
```

### Rotas Públicas

As seguintes rotas não requerem autenticação:

- `POST /api/recepcionista/cadastro` - Cadastro de recepcionista

### Como Logar

1. **Registre um recepcionista** (sem autenticação):
   ```bash
   POST /api/recepcionista/cadastro
   ```

2. **Use as credenciais** (email e senha) para acessar outros endpoints

3. **No Postman ou similar**, configure:
   - Authorization → Type: Basic Auth
   - Username: seu_email@example.com
   - Password: sua_senha

---

## 🌐 Endpoints da API

### Base URL
```
http://localhost:8080
```

### Pacientes

#### 1. Criar Paciente
```http
POST /api/paciente/cadastro
Content-Type: application/json
Authorization: Basic base64(email:senha)

{
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "telefone": "(11) 98765-4321",
  "endereco": "Rua das Flores, 123",
  "dataNascimento": "1990-05-15",
  "genero": "MASCULINO",
  "usuario": {
    "email": "joao@example.com",
    "senha": "senha123"
  }
}
```

**Resposta (201 Created):**
```json
{
  "nome": "João Silva",
  "dataNascimento": "1990-05-15",
  "genero": "MASCULINO"
}
```

#### 2. Atualizar Paciente
```http
PUT /api/paciente/atualizar/{id}
Content-Type: application/json
Authorization: Basic base64(email:senha)

{
  "nome": "João Silva Atualizado",
  "cpf": "123.456.789-00",
  "telefone": "(11) 98765-4322",
  "endereco": "Rua das Flores, 456",
  "dataNascimento": "1990-05-15",
  "genero": "MASCULINO",
  "usuario": {
    "email": "joao@example.com",
    "senha": "senha123"
  }
}
```

**Resposta (200 OK):**
```json
{
  "nome": "João Silva Atualizado",
  "dataNascimento": "1990-05-15",
  "genero": "MASCULINO"
}
```

#### 3. Buscar Paciente por CPF
```http
GET /api/paciente/busca/123.456.789-00
Authorization: Basic base64(email:senha)
```

**Resposta (200 OK):**
```json
{
  "nome": "João Silva",
  "dataNascimento": "1990-05-15",
  "genero": "MASCULINO"
}
```

#### 4. Listar Todos os Pacientes
```http
GET /api/paciente/busca/all
Authorization: Basic base64(email:senha)
```

**Resposta (200 OK):**
```json
[
  {
    "nome": "João Silva",
    "dataNascimento": "1990-05-15",
    "genero": "MASCULINO"
  },
  {
    "nome": "Maria Santos",
    "dataNascimento": "1985-08-22",
    "genero": "FEMININO"
  }
]
```

#### 5. Excluir Paciente
```http
DELETE /api/paciente/delete/{id}
Authorization: Basic base64(email:senha)
```

**Resposta (204 No Content)**

---

### Médicos

#### 1. Criar Médico
```http
POST /api/medicos/cadastro
Content-Type: application/json
Authorization: Basic base64(email:senha)

{
  "nome": "Dr. Carlos Mendes",
  "telefone": "(11) 99999-8888",
  "crm": "123456",
  "especialidade": "CARDIOLOGIA",
  "dataContratacao": "2023-01-15",
  "status": true,
  "usuario": {
    "email": "carlos@example.com",
    "senha": "senha123"
  }
}
```

**Resposta (201 Created):**
```json
{
  "nome": "Dr. Carlos Mendes",
  "crm": "123456",
  "especialidade": "CARDIOLOGIA",
  "dataContratacao": "2023-01-15",
  "status": true
}
```

#### 2. Atualizar Médico
```http
PUT /api/medicos/atualizar/{id}
Content-Type: application/json
Authorization: Basic base64(email:senha)

{
  "nome": "Dr. Carlos Mendes",
  "telefone": "(11) 99999-8889",
  "crm": "123456",
  "especialidade": "CARDIOLOGIA",
  "dataContratacao": "2023-01-15",
  "status": true,
  "usuario": {
    "email": "carlos@example.com",
    "senha": "senha123"
  }
}
```

**Resposta (200 OK):**
```json
{
  "nome": "Dr. Carlos Mendes",
  "crm": "123456",
  "especialidade": "CARDIOLOGIA",
  "dataContratacao": "2023-01-15",
  "status": true
}
```

#### 3. Buscar Médico por CRM
```http
GET /api/medicos/busca/crm/123456
Authorization: Basic base64(email:senha)
```

**Resposta (200 OK):**
```json
{
  "nome": "Dr. Carlos Mendes",
  "crm": "123456",
  "especialidade": "CARDIOLOGIA",
  "dataContratacao": "2023-01-15",
  "status": true
}
```

#### 4. Buscar Médicos por Nome
```http
GET /api/medicos/busca/Carlos
Authorization: Basic base64(email:senha)
```

**Resposta (200 OK):**
```json
[
  {
    "nome": "Dr. Carlos Mendes",
    "crm": "123456",
    "especialidade": "CARDIOLOGIA",
    "dataContratacao": "2023-01-15",
    "status": true
  }
]
```

#### 5. Listar Todos os Médicos
```http
GET /api/medicos/busca/all
Authorization: Basic base64(email:senha)
```

**Resposta (200 OK):**
```json
[
  {
    "nome": "Dr. Carlos Mendes",
    "crm": "123456",
    "especialidade": "CARDIOLOGIA",
    "dataContratacao": "2023-01-15",
    "status": true
  }
]
```

#### 6. Excluir Médico
```http
DELETE /api/medicos/delete/{id}
Authorization: Basic base64(email:senha)
```

**Resposta (204 No Content)**

#### Especialidades Disponíveis
```
CARDIOLOGIA
DERMATOLOGIA
OFTALMOLOGIA
ORTOPEDIA
PEDIATRIA
PSIQUIATRIA
NEUROLOGIA
GINECOLOGIA
PNEUMOLOGIA
GASTROENTEROLOGIA
```

---

### Recepcionistas

#### 1. Cadastrar Recepcionista (Público)
```http
POST /api/recepcionista/cadastro
Content-Type: application/json

{
  "cpf": "987.654.321-00",
  "nome": "Ana Silva",
  "telefone": "(11) 91234-5678",
  "dataContratacao": "2023-06-01",
  "status": true,
  "usuario": {
    "email": "ana@example.com",
    "senha": "senha123"
  }
}
```

**Resposta (201 Created):**
```json
{
  "nome": "Ana Silva",
  "dataContratacao": "2023-06-01",
  "status": true
}
```

#### 2. Atualizar Recepcionista
```http
PUT /api/recepcionista/atualizar/{id}
Content-Type: application/json
Authorization: Basic base64(email:senha)

{
  "cpf": "987.654.321-00",
  "nome": "Ana Silva",
  "telefone": "(11) 91234-5679",
  "dataContratacao": "2023-06-01",
  "status": true,
  "usuario": {
    "email": "ana@example.com",
    "senha": "senha123"
  }
}
```

**Resposta (200 OK):**
```json
{
  "nome": "Ana Silva",
  "dataContratacao": "2023-06-01",
  "status": true
}
```

#### 3. Buscar Recepcionista por CPF
```http
GET /api/recepcionista/busca/987.654.321-00
Authorization: Basic base64(email:senha)
```

**Resposta (200 OK):**
```json
{
  "nome": "Ana Silva",
  "dataContratacao": "2023-06-01",
  "status": true
}
```

#### 4. Listar Todos os Recepcionistas
```http
GET /api/recepcionista/busca/all
Authorization: Basic base64(email:senha)
```

**Resposta (200 OK):**
```json
[
  {
    "nome": "Ana Silva",
    "dataContratacao": "2023-06-01",
    "status": true
  }
]
```

#### 5. Excluir Recepcionista
```http
DELETE /api/recepcionista/delete/{id}
Authorization: Basic base64(email:senha)
```

**Resposta (204 No Content)**

---

### Consultas

#### 1. Agendar Consulta
```http
POST /api/consulta/agendar
Content-Type: application/json
Authorization: Basic base64(email:senha)

{
  "pacienteId": "550e8400-e29b-41d4-a716-446655440000",
  "medicoId": "550e8400-e29b-41d4-a716-446655440001",
  "recepcionistaId": "550e8400-e29b-41d4-a716-446655440002",
  "dataHora": "2024-09-15T10:30:00",
  "observacoes": "Primeira consulta para avaliação"
}
```

**Resposta (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440003",
  "pacienteNome": "João Silva",
  "medicoNome": "Dr. Carlos Mendes",
  "recepcionistaNome": "Ana Silva",
  "dataHora": "2024-09-15T10:30:00",
  "status": "AGENDADA",
  "motivoCancelamento": null,
  "observacoes": "Primeira consulta para avaliação"
}
```

#### 2. Atualizar Status da Consulta
```http
PUT /api/consulta/status/{id}
Content-Type: application/json
Authorization: Basic base64(email:senha)

"CONCLUIDA"
```

**Resposta (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440003",
  "pacienteNome": "João Silva",
  "medicoNome": "Dr. Carlos Mendes",
  "recepcionistaNome": "Ana Silva",
  "dataHora": "2024-09-15T10:30:00",
  "status": "CONCLUIDA",
  "motivoCancelamento": null,
  "observacoes": "Primeira consulta para avaliação"
}
```

#### 3. Buscar Consultas por Paciente
```http
GET /api/consulta/paciente/{pacienteId}
Authorization: Basic base64(email:senha)
```

**Resposta (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "pacienteNome": "João Silva",
    "medicoNome": "Dr. Carlos Mendes",
    "recepcionistaNome": "Ana Silva",
    "dataHora": "2024-09-15T10:30:00",
    "status": "CONCLUIDA",
    "motivoCancelamento": null,
    "observacoes": "Primeira consulta para avaliação"
  }
]
```

#### Status Disponíveis
```
AGENDADA
CONCLUIDA
CANCELADA
NAO_COMPARECEU
```

---

### Histórico Médico

#### 1. Criar Histórico Médico
```http
POST /api/historico-medico/criar/{pacienteId}/{medicoId}/{consultaId}
Content-Type: application/json
Authorization: Basic base64(email:senha)

{
  "diagnostico": "Hipertensão arterial",
  "prescricao": "Losartana 50mg - 1 comprimido ao dia",
  "observacoes": "Paciente apresenta pressão elevada, recomendado acompanhamento mensal"
}
```

**Resposta (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440004",
  "pacienteId": "550e8400-e29b-41d4-a716-446655440000",
  "medicoId": "550e8400-e29b-41d4-a716-446655440001",
  "consultaId": "550e8400-e29b-41d4-a716-446655440003",
  "diagnostico": "Hipertensão arterial",
  "prescricao": "Losartana 50mg - 1 comprimido ao dia",
  "observacoes": "Paciente apresenta pressão elevada, recomendado acompanhamento mensal",
  "dataCriacao": "2024-09-15T10:45:00"
}
```

#### 2. Adicionar Exame ao Histórico Médico
```http
POST /api/historico-medico/adicionar-exame/{historicoMedicoId}
Content-Type: application/json
Authorization: Basic base64(email:senha)

{
  "tipoExame": "Eletrocardiograma",
  "resultado": "Normal",
  "dataExame": "2024-09-15T11:00:00",
  "arquivoUrl": "https://storage.example.com/exames/ecg_123.pdf"
}
```

**Resposta (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440005",
  "tipoExame": "Eletrocardiograma",
  "resultado": "Normal",
  "dataExame": "2024-09-15T11:00:00",
  "arquivoUrl": "https://storage.example.com/exames/ecg_123.pdf"
}
```

---

### Próximos Passos para Testes

- [ ] Implementar testes para todos os Services
- [ ] Adicionar testes para Controllers
- [ ] Criar testes de integração
- [ ] Configurar CI/CD para rodar testes automaticamente
- [ ] Aumentar cobertura de testes para 80%+

---

## 📌 Notas Importantes

### 1. Segurança

- ⚠️ **CSRF Desabilitado**: A configuração de segurança atualmente desabilita CSRF para simplificar o desenvolvimento. **Em produção**, isso deve ser adequadamente configurado.
- 🔒 **Usar HTTPS**: Em produção, sempre use HTTPS.
- 🔑 **Senhas**: As senhas são hashadas com BCrypt (BCryptPasswordEncoder).

### 2. Banco de Dados

- O Hibernate está configurado com `ddl-auto: update`, que atualiza automaticamente o schema.
- Em produção, considere usar migrations com Liquibase ou Flyway.
- SQL formatado é exibido nos logs (`show-sql: true`).

### 3. Validação

- Todos os DTOs utilizam `@Valid` e anotações de validação do Jakarta.
- Campos obrigatórios são validados automaticamente.

### 4. MapStruct

- Utilizamos MapStruct para mapear DTOs ↔ Entities de forma type-safe.
- Os mappers são gerados em tempo de compilação (zero overhead de runtime).

### 5. Documentação da API

Para gerar documentação Swagger/OpenAPI (a implementar):

```xml
<!-- Adicionar ao pom.xml -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.0</version>
</dependency>
```

Depois acessar: `http://localhost:8080/swagger-ui.html`

---

## 🚀 Roadmap

- [x] Setup inicial do projeto
- [x] Implementação de controladores básicos
- [x] Configuração de segurança (HTTP Basic Auth)
- [ ] Implementar todos os testes unitários com JUnit e Mockito
- [ ] Adicionar autenticação JWT
- [ ] Gerar documentação Swagger/OpenAPI
- [ ] Configurar CI/CD (GitHub Actions)
- [ ] Deploy em produção (Docker + Kubernetes)
- [ ] Implementar cache

---
