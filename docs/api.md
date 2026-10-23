# API: exemplos e detalhes

Todas as rotas usam JSON e HTTP Basic. As respostas de erro seguem o formato:

```json
{
  "status": 404,
  "mensagem": "Paciente não encontrado.",
  "timestamp": "2026-10-23T17:30:00",
  "campos": {
    "cpf": "CPF inválido. Deve conter 11 dígitos, sem pontuação."
  }
}
```

`campos` aparece apenas em erros de validação. Códigos usados:

- `201` criação
- `204` exclusão
- `400` validação ou regra de negócio
- `401` sem credencial ou credencial inválida
- `403` papel sem permissão
- `404` recurso não encontrado
- `409` CPF, CRM ou e-mail duplicado
- `500` erro inesperado

## Recepcionistas

### Cadastrar

`POST /recepcionista/cadastro` (ADMIN)

```json
{
  "cpf": "98765432100",
  "nome": "Ana Silva",
  "telefone": "(11) 91234-5678",
  "dataContratacao": "2023-06-01",
  "status": true,
  "usuario": {
    "email": "ana@example.com",
    "senha": "123456"
  }
}
```

Resposta `201`:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nome": "Ana Silva",
  "cpf": "98765432100",
  "dataContratacao": "2023-06-01",
  "status": true
}
```

### Atualizar

`PUT /recepcionista/atualizar/{id}` (ADMIN)

O corpo é o mesmo do cadastro. O e-mail e a senha do usuário não são alterados por este endpoint.

### Buscar e listar

- `GET /recepcionista/busca/{cpf}` (ADMIN)
- `GET /recepcionista/busca/all` (ADMIN)

### Excluir

`DELETE /recepcionista/delete/{id}` (ADMIN). Resposta `204`.

## Médicos

### Cadastrar

`POST /medicos/cadastro` (ADMIN)

```json
{
  "nome": "Dr. Carlos Mendes",
  "telefone": "(11) 99999-8888",
  "crm": "123456",
  "especialidade": "CARDIOLOGIA",
  "dataContratacao": "2023-01-15",
  "status": true,
  "usuario": {
    "email": "carlos@example.com",
    "senha": "123456"
  }
}
```

Resposta `201`:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "nome": "Dr. Carlos Mendes",
  "crm": "123456",
  "especialidade": "CARDIOLOGIA",
  "dataContratacao": "2023-01-15",
  "status": true
}
```

Especialidades: `CARDIOLOGIA`, `PEDIATRIA`, `ORTOPEDIA`, `GINECOLOGIA`, `DERMATOLOGIA`.

### Atualizar

`PUT /medicos/atualizar/{id}` (ADMIN). O corpo é o mesmo do cadastro.

### Buscar e listar

- `GET /medicos/busca/{nome}` (qualquer autenticado)
- `GET /medicos/busca/crm/{crm}` (qualquer autenticado)
- `GET /medicos/busca/all` (qualquer autenticado)

### Excluir

`DELETE /medicos/delete/{id}` (ADMIN). Resposta `204`.

## Pacientes

### Cadastrar

`POST /paciente/cadastro` (ADMIN ou RECEPCIONISTA)

```json
{
  "nome": "João Silva",
  "cpf": "12345678901",
  "telefone": "(11) 98765-4321",
  "endereco": "Rua das Flores, 123",
  "dataNascimento": "1990-05-15",
  "genero": "MASCULINO",
  "usuario": {
    "email": "joao@example.com",
    "senha": "123456"
  }
}
```

Resposta `201`:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "nome": "João Silva",
  "cpf": "12345678901",
  "dataNascimento": "1990-05-15",
  "genero": "MASCULINO"
}
```

Gêneros: `MASCULINO`, `FEMININO`, `OUTROS`.

### Atualizar

`PUT /paciente/atualizar/{id}` (ADMIN ou RECEPCIONISTA). O corpo é o mesmo do cadastro.

### Buscar e listar

- `GET /paciente/busca/{cpf}` (ADMIN ou RECEPCIONISTA)
- `GET /paciente/busca/all` (ADMIN ou RECEPCIONISTA)

### Excluir

`DELETE /paciente/delete/{id}` (ADMIN). Resposta `204`.

## Consultas

### Agendar

`POST /consulta/agendar` (ADMIN ou RECEPCIONISTA)

```json
{
  "pacienteId": "550e8400-e29b-41d4-a716-446655440002",
  "medicoId": "550e8400-e29b-41d4-a716-446655440001",
  "recepcionistaId": "550e8400-e29b-41d4-a716-446655440000",
  "dataHora": "2026-12-01T10:30:00",
  "observacoes": "Primeira consulta"
}
```

`dataHora` precisa ser hoje ou uma data futura.

Resposta `201`:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440003",
  "pacienteNome": "João Silva",
  "medicoNome": "Dr. Carlos Mendes",
  "recepcionistaNome": "Ana Silva",
  "dataHora": "2026-12-01T10:30:00",
  "status": "AGENDADA",
  "motivoCancelamento": null,
  "observacoes": "Primeira consulta"
}
```

### Alterar status

`PUT /consulta/status/{id}` (ADMIN ou RECEPCIONISTA). O corpo é uma string JSON:

```json
"CANCELADA"
```

Status: `AGENDADA`, `CANCELADA`, `REALIZADA`, `FINALIZADA`.

### Buscar por paciente

`GET /consulta/paciente/{pacienteId}` (ADMIN, RECEPCIONISTA ou MEDICO). Retorna uma lista de consultas.

## Histórico médico

### Criar

`POST /historico-medico/criar/{pacienteId}/{medicoId}/{consultaId}` (MEDICO ou ADMIN)

```json
{
  "diagnostico": "Hipertensão arterial",
  "prescricao": "Losartana 50mg, 1 comprimido ao dia",
  "observacoes": "Acompanhamento mensal"
}
```

Resposta `201`:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440004",
  "pacienteNome": "João Silva",
  "medicoNome": "Dr. Carlos Mendes",
  "consultaStatus": "REALIZADA",
  "diagnostico": "Hipertensão arterial",
  "prescricao": "Losartana 50mg, 1 comprimido ao dia",
  "observacoes": "Acompanhamento mensal",
  "dataRegistro": "2026-12-01T10:45:00"
}
```

### Adicionar exame ao histórico

`POST /historico-medico/adicionar-exame/{historicoMedicoId}` (MEDICO ou ADMIN)

```json
{
  "tipoExame": "Eletrocardiograma",
  "resultado": "Normal",
  "dataExame": "2026-12-01T11:00:00",
  "arquivoUrl": "https://storage.example.com/exames/ecg_123.pdf"
}
```

Resposta `201`:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440005",
  "tipoExame": "Eletrocardiograma",
  "resultado": "Normal",
  "dataExame": "2026-12-01T11:00:00",
  "arquivoUrl": "https://storage.example.com/exames/ecg_123.pdf"
}
```

## Exames

`POST /exame-medico/adicionar/{historicoMedicoId}` (MEDICO ou ADMIN)

O corpo e a resposta são os mesmos do endpoint de adicionar exame ao histórico.

## Validação

- `nome`: obrigatório, até 255 caracteres.
- `cpf`: obrigatório, exatamente 11 dígitos, sem pontuação.
- `telefone`: opcional, formato `(XX) XXXXX-XXXX`.
- `crm`: obrigatório, de 4 a 6 dígitos.
- `email`: obrigatório e válido.
- `senha`: obrigatória, de 6 a 100 caracteres.
- `dataNascimento`: data passada.
- `dataHora` da consulta: hoje ou futura.
- `diagnostico`, `tipoExame` e `resultado`: obrigatórios.

## Autenticação

```bash
curl -u admin@consultorio.local:troque_esta_senha_admin http://localhost:8080/paciente/busca/all
```

O usuário ADMIN inicial vem de `ADMIN_EMAIL` e `ADMIN_PASSWORD`. No Swagger, use o botão Authorize e informe e-mail e senha.
