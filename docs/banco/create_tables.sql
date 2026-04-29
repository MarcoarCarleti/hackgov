CREATE TABLE IF NOT EXISTS ubs (
    id BIGSERIAL,
    nome VARCHAR(150) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_ubs PRIMARY KEY (id),
    CONSTRAINT ck_ubs_estado CHECK (estado ~ '^[A-Z]{2}$')
);

CREATE TABLE IF NOT EXISTS especialidade (
    id BIGSERIAL,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_especialidade PRIMARY KEY (id),
    CONSTRAINT uk_especialidade_nome UNIQUE (nome)
);

CREATE TABLE IF NOT EXISTS profissional (
    id BIGSERIAL,
    nome VARCHAR(150) NOT NULL,
    registro_conselho VARCHAR(30) NOT NULL,
    ubs_id BIGINT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_profissional PRIMARY KEY (id),
    CONSTRAINT uk_profissional_registro_conselho UNIQUE (registro_conselho),
    CONSTRAINT fk_profissional_ubs FOREIGN KEY (ubs_id) REFERENCES ubs (id)
);

CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    cartao_sus VARCHAR(15) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL,
    ubs_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uk_usuario_cpf UNIQUE (cpf),
    CONSTRAINT uk_usuario_cartao_sus UNIQUE (cartao_sus),
    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT fk_usuario_ubs FOREIGN KEY (ubs_id) REFERENCES ubs (id),
    CONSTRAINT ck_usuario_cpf CHECK (cpf ~ '^[0-9]{11}$'),
    CONSTRAINT ck_usuario_cartao_sus CHECK (cartao_sus ~ '^[0-9]{15}$'),
    CONSTRAINT ck_usuario_tipo CHECK (tipo_usuario IN ('PACIENTE', 'GESTOR', 'ADMIN'))
);

CREATE TABLE IF NOT EXISTS profissional_especialidade (
    id BIGSERIAL,
    profissional_id BIGINT NOT NULL,
    especialidade_id BIGINT NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_profissional_especialidade PRIMARY KEY (id),
    CONSTRAINT uk_profissional_especialidade UNIQUE (profissional_id, especialidade_id),
    CONSTRAINT fk_profissional_especialidade_profissional FOREIGN KEY (profissional_id) REFERENCES profissional (id),
    CONSTRAINT fk_profissional_especialidade_especialidade FOREIGN KEY (especialidade_id) REFERENCES especialidade (id)
);

CREATE TABLE IF NOT EXISTS agenda_profissional (
    id BIGSERIAL,
    profissional_id BIGINT NOT NULL,
    especialidade_id BIGINT NOT NULL,
    data_agenda DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_agenda_profissional PRIMARY KEY (id),
    CONSTRAINT uk_agenda_profissional_profissional_data_hora UNIQUE (profissional_id, data_agenda, hora_inicio),
    CONSTRAINT fk_agenda_profissional_profissional FOREIGN KEY (profissional_id) REFERENCES profissional (id),
    CONSTRAINT fk_agenda_profissional_especialidade FOREIGN KEY (especialidade_id) REFERENCES especialidade (id),
    CONSTRAINT ck_agenda_profissional_horario CHECK (hora_fim > hora_inicio)
);

CREATE TABLE IF NOT EXISTS agendamento (
    id BIGSERIAL,
    usuario_id BIGINT NOT NULL,
    agenda_profissional_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    observacoes VARCHAR(500),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelado_em TIMESTAMPTZ,
    motivo_cancelamento VARCHAR(255),
    encaixe_automatico BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_agendamento PRIMARY KEY (id),
    CONSTRAINT fk_agendamento_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
    CONSTRAINT fk_agendamento_agenda_profissional FOREIGN KEY (agenda_profissional_id) REFERENCES agenda_profissional (id),
    CONSTRAINT ck_agendamento_status CHECK (
        status IN (
            'AGENDADA',
            'CANCELADA_PELO_PACIENTE',
            'CANCELADA_PELO_SISTEMA',
            'REAGENDADA',
            'REALIZADA',
            'FALTA',
            'ENCAIXADA'
        )
    ),
    CONSTRAINT ck_agendamento_cancelamento CHECK (
        status NOT IN ('CANCELADA_PELO_PACIENTE', 'CANCELADA_PELO_SISTEMA')
        OR cancelado_em IS NOT NULL
    )
);
