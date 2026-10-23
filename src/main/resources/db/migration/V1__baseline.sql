CREATE TABLE usuarios (
    id uuid NOT NULL,
    email varchar(255) NOT NULL,
    senha varchar(255) NOT NULL,
    role varchar(255),
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_email UNIQUE (email)
);

CREATE TABLE pacientes (
    id uuid NOT NULL,
    usuario_id uuid,
    cpf varchar(11) NOT NULL,
    nome varchar(255) NOT NULL,
    telefone varchar(255),
    endereco varchar(255),
    data_nascimento date,
    genero varchar(255),
    data_cadastro timestamp(6),
    CONSTRAINT pk_pacientes PRIMARY KEY (id),
    CONSTRAINT uk_pacientes_cpf UNIQUE (cpf),
    CONSTRAINT fk_pacientes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

CREATE TABLE medicos (
    id uuid NOT NULL,
    usuario_id uuid,
    nome varchar(255) NOT NULL,
    telefone varchar(255),
    crm varchar(255) NOT NULL,
    especialidade varchar(255),
    data_contratacao date NOT NULL,
    status boolean NOT NULL,
    CONSTRAINT pk_medicos PRIMARY KEY (id),
    CONSTRAINT uk_medicos_crm UNIQUE (crm),
    CONSTRAINT fk_medicos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

CREATE TABLE recepcionistas (
    id uuid NOT NULL,
    usuario_id uuid,
    cpf varchar(11) NOT NULL,
    nome varchar(255) NOT NULL,
    telefone varchar(255),
    data_contratacao date,
    status boolean NOT NULL,
    CONSTRAINT pk_recepcionistas PRIMARY KEY (id),
    CONSTRAINT uk_recepcionistas_cpf UNIQUE (cpf),
    CONSTRAINT fk_recepcionistas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

CREATE TABLE consultas (
    id uuid NOT NULL,
    medico_id uuid,
    paciente_id uuid,
    recepcionista_id uuid,
    data_hora timestamp(6) NOT NULL,
    status varchar(255) NOT NULL,
    motivo_cancelamento varchar(255),
    observacoes varchar(255),
    CONSTRAINT pk_consultas PRIMARY KEY (id),
    CONSTRAINT fk_consultas_medico FOREIGN KEY (medico_id) REFERENCES medicos (id),
    CONSTRAINT fk_consultas_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes (id),
    CONSTRAINT fk_consultas_recepcionista FOREIGN KEY (recepcionista_id) REFERENCES recepcionistas (id)
);

CREATE TABLE historico_medico (
    id uuid NOT NULL,
    paciente_id uuid,
    medico_id uuid,
    consulta_id uuid,
    diagnostico varchar(255) NOT NULL,
    prescricao varchar(255),
    observacoes varchar(255),
    data_registro timestamp(6) NOT NULL,
    CONSTRAINT pk_historico_medico PRIMARY KEY (id),
    CONSTRAINT fk_historico_medico_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes (id),
    CONSTRAINT fk_historico_medico_medico FOREIGN KEY (medico_id) REFERENCES medicos (id),
    CONSTRAINT fk_historico_medico_consulta FOREIGN KEY (consulta_id) REFERENCES consultas (id)
);

CREATE TABLE exames_medicos (
    id uuid NOT NULL,
    historico_medico_id uuid,
    tipo_exame varchar(255) NOT NULL,
    resultado varchar(255) NOT NULL,
    data_exame timestamp(6) NOT NULL,
    arquivo_url varchar(255),
    CONSTRAINT pk_exames_medicos PRIMARY KEY (id),
    CONSTRAINT fk_exames_medicos_historico FOREIGN KEY (historico_medico_id) REFERENCES historico_medico (id)
);
