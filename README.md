# Consultório Médico API

[![CI](https://github.com/rodvpx/consultorio-medico-api/actions/workflows/ci.yml/badge.svg)](https://github.com/rodvpx/consultorio-medico-api/actions/workflows/ci.yml)

API REST para gestão de um consultório médico: pacientes, médicos, recepcionistas, consultas, histórico médico e exames. Projeto de portfólio com autenticação HTTP Basic, autorização por papéis, migrations com Flyway, testes automatizados e documentação OpenAPI.

## Stack

- Java 21 e Spring Boot 4.1
- Spring Web, Spring Data JPA, Spring Security e Bean Validation
- PostgreSQL 17, Flyway e Hibernate
- MapStruct e Lombok
- JUnit 5, Mockito, MockMvc, Spring Security Test e JaCoCo
- Maven Wrapper, Docker e Docker Compose
- springdoc-openapi (Swagger UI)

## Modelo de dados

- `Usuario`: credenciais e papel (`ADMIN`, `MEDICO`, `RECEPCIONISTA`, `PACIENTE`).
- `Paciente`: dados pessoais, CPF único e usuário associado.
- `Medico`: CRM único, especialidade e usuário associado.
- `Recepcionista`: CPF único e usuário associado.
- `Consulta`: paciente, médico, recepcionista, data/hora e status.
- `HistoricoMedico`: paciente, médico, consulta, diagnóstico e prescrição.
- `ExameMedico`: exame vinculado a um histórico médico.

```text
Usuario 1---N Paciente / Medico / Recepcionista
Paciente 1---N Consulta N---1 Medico
Consulta 1---N HistoricoMedico N---1 Paciente
HistoricoMedico 1---N ExameMedico
```

## Como rodar

Pré-requisito: Docker com Compose.

```bash
cp .env.example .env
# edite o .env e troque as senhas
docker compose up -d --build
```

A API sobe em `http://localhost:8080`. A documentação interativa fica em `http://localhost:8080/swagger-ui/index.html`.

Para parar:

```bash
docker compose down
```

Para recriar o banco do zero (apaga os dados):

```bash
docker compose down -v
```

## Variáveis de ambiente

| Variável | Uso |
| --- | --- |
| `POSTGRES_DB` | Nome do banco criado pelo container |
| `POSTGRES_USER` | Usuário do PostgreSQL |
| `POSTGRES_PASSWORD` | Senha do PostgreSQL |
| `ADMIN_EMAIL` | E-mail do primeiro usuário ADMIN |
| `ADMIN_PASSWORD` | Senha do primeiro usuário ADMIN |

O `.env` não é versionado. Use o `.env.example` como modelo. O ADMIN inicial é criado na primeira inicialização apenas se o e-mail ainda não existir. Depois disso, novos recepcionistas e médicos são cadastrados por um ADMIN.

## Autenticação e permissões

A API usa HTTP Basic com sessão stateless. As senhas são armazenadas com BCrypt e nunca aparecem nas respostas.

Papéis:

- `ADMIN`: administra usuários e acessa todos os recursos.
- `RECEPCIONISTA`: cadastra e consulta pacientes, agenda consultas e altera status.
- `MEDICO`: consulta médicos, consultas por paciente, históricos e exames.
- `PACIENTE`: papel legado para usuários de pacientes já existentes, sem permissões administrativas.

### Matriz de permissões

| Recurso | Endpoint | Papéis |
| --- | --- | --- |
| Recepcionista | `POST /recepcionista/cadastro` | ADMIN |
| Recepcionista | `PUT /recepcionista/atualizar/{id}` | ADMIN |
| Recepcionista | `GET /recepcionista/busca/{cpf}` e `/busca/all` | ADMIN |
| Recepcionista | `DELETE /recepcionista/delete/{id}` | ADMIN |
| Médico | `POST /medico/cadastro` | ADMIN |
| Médico | `PUT /medico/atualizar/{id}` | ADMIN |
| Médico | `DELETE /medico/delete/{id}` | ADMIN |
| Médico | `GET /medico/busca/{nome}`, `/busca/crm/{crm}`, `/busca/all` | Qualquer usuário autenticado |
| Paciente | `POST /paciente/cadastro` | ADMIN, RECEPCIONISTA |
| Paciente | `PUT /paciente/atualizar/{id}` | ADMIN, RECEPCIONISTA |
| Paciente | `GET /paciente/busca/{cpf}` e `/busca/all` | ADMIN, RECEPCIONISTA |
| Paciente | `DELETE /paciente/delete/{id}` | ADMIN |
| Consulta | `POST /consulta/agendar` | ADMIN, RECEPCIONISTA |
| Consulta | `PUT /consulta/status/{id}` | ADMIN, RECEPCIONISTA |
| Consulta | `GET /consulta/paciente/{pacienteId}` | ADMIN, RECEPCIONISTA, MEDICO |
| Histórico | `POST /historico-medico/criar/{pacienteId}/{medicoId}/{consultaId}` | MEDICO, ADMIN |
| Histórico | `POST /historico-medico/adicionar-exame/{historicoMedicoId}` | MEDICO, ADMIN |
| Exame | `POST /exame-medico/adicionar/{historicoMedicoId}` | MEDICO, ADMIN |

As rotas de Swagger (`/swagger-ui/**` e `/v3/api-docs/**`) são públicas. Todas as demais exigem autenticação.

## Endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| POST | `/recepcionista/cadastro` | Cadastra recepcionista |
| PUT | `/recepcionista/atualizar/{id}` | Atualiza recepcionista |
| GET | `/recepcionista/busca/{cpf}` | Busca recepcionista por CPF |
| GET | `/recepcionista/busca/all` | Lista recepcionistas |
| DELETE | `/recepcionista/delete/{id}` | Exclui recepcionista |
| POST | `/medico/cadastro` | Cadastra médico |
| PUT | `/medico/atualizar/{id}` | Atualiza médico |
| GET | `/medico/busca/{nome}` | Busca médicos por nome |
| GET | `/medico/busca/crm/{crm}` | Busca médico por CRM |
| GET | `/medico/busca/all` | Lista médicos |
| DELETE | `/medico/delete/{id}` | Exclui médico |
| POST | `/paciente/cadastro` | Cadastra paciente |
| PUT | `/paciente/atualizar/{id}` | Atualiza paciente |
| GET | `/paciente/busca/{cpf}` | Busca paciente por CPF |
| GET | `/paciente/busca/all` | Lista pacientes |
| DELETE | `/paciente/delete/{id}` | Exclui paciente |
| POST | `/consulta/agendar` | Agenda consulta |
| PUT | `/consulta/status/{id}` | Altera status da consulta |
| GET | `/consulta/paciente/{pacienteId}` | Lista consultas do paciente |
| POST | `/historico-medico/criar/{pacienteId}/{medicoId}/{consultaId}` | Cria histórico |
| POST | `/historico-medico/adicionar-exame/{historicoMedicoId}` | Adiciona exame ao histórico |
| POST | `/exame-medico/adicionar/{historicoMedicoId}` | Adiciona exame ao histórico |

Exemplos de payloads, respostas, enums e formato de erro: [docs/api.md](docs/api.md).

## Fluxo de exemplo com curl

O exemplo usa `jq` para extrair os IDs retornados.

```bash
BASE=http://localhost:8080
ADMIN=admin@consultorio.local:troque_esta_senha_admin

# 1. Cadastra uma recepcionista como ADMIN
RECEP=$(curl -s -u "$ADMIN" -H 'Content-Type: application/json' \
  -d '{"cpf":"98765432100","nome":"Ana","telefone":"(11) 91234-5678","dataContratacao":"2023-01-01","status":true,"usuario":{"email":"ana@consultorio.local","senha":"123456"}}' \
  "$BASE/recepcionista/cadastro")
RECEP_ID=$(echo "$RECEP" | jq -r '.id')

# 2. Cadastra um paciente como recepcionista
PAC=$(curl -s -u ana@consultorio.local:123456 -H 'Content-Type: application/json' \
  -d '{"nome":"João","cpf":"12345678901","telefone":"(11) 91234-5678","dataNascimento":"1990-01-01","genero":"MASCULINO","usuario":{"email":"joao@consultorio.local","senha":"123456"}}' \
  "$BASE/paciente/cadastro")
PAC_ID=$(echo "$PAC" | jq -r '.id')

# 3. Cadastra um médico como ADMIN
MED=$(curl -s -u "$ADMIN" -H 'Content-Type: application/json' \
  -d '{"nome":"Dr. Carlos","telefone":"(11) 91234-5678","crm":"123456","especialidade":"CARDIOLOGIA","dataContratacao":"2023-01-01","status":true,"usuario":{"email":"carlos@consultorio.local","senha":"123456"}}' \
  "$BASE/medico/cadastro")
MED_ID=$(echo "$MED" | jq -r '.id')

# 4. Agenda uma consulta como recepcionista
curl -u ana@consultorio.local:123456 -H 'Content-Type: application/json' \
  -d "{\"pacienteId\":\"$PAC_ID\",\"medicoId\":\"$MED_ID\",\"recepcionistaId\":\"$RECEP_ID\",\"dataHora\":\"2026-12-01T10:00:00\",\"observacoes\":\"Primeira consulta\"}" \
  "$BASE/consulta/agendar"
```

## Testes e cobertura

```bash
./mvnw clean verify
```

O comando executa os testes unitários, os testes de segurança com MockMvc, gera o relatório JaCoCo e empacota a aplicação. O relatório fica em `target/site/jacoco/index.html`.

Os testes usam H2 em memória no perfil `test`, com Flyway desabilitado. Nenhum banco externo é necessário.

## Banco de dados e migrations

O schema é versionado pelo Flyway em `src/main/resources/db/migration`. O Hibernate está em `ddl-auto: validate`, então a aplicação não altera o schema sozinha. O projeto é novo e não usa baseline: se o banco já tiver tabelas sem o histórico do Flyway, a inicialização falha de forma explícita em vez de pular a V1. Se o schema local estiver divergente, recrie o volume com `docker compose down -v`.

## Segurança

- HTTP Basic em toda requisição, sem sessão no servidor (`SessionCreationPolicy.STATELESS`).
- CSRF desativado porque a API é stateless e não usa cookies de sessão. Isso é aceitável para Basic Auth em API, mas exige HTTPS em produção. Se a autenticação passar a usar cookies, o CSRF deve ser reativado.
- JWT e refresh token estão fora do escopo deste projeto.
- Erros de autenticação e autorização retornam JSON padronizado com status 401 ou 403.
- O hash de senha não é exposto em nenhum DTO de resposta.

## Estrutura

```text
src/main/java/br/com/rodrigo/consultorio_medico_api/
├── config/       # segurança, OpenAPI e ADMIN inicial
├── controller/   # endpoints REST
├── dto/          # requests e responses
├── entity/       # entidades JPA e enum de papel
├── exception/    # exceções e tratamento global
├── mapper/       # MapStruct
├── repository/   # Spring Data JPA
└── service/      # regras de negócio e UserDetailsService
```

Documentação detalhada da API: [docs/api.md](docs/api.md).
