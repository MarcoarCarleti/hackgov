# Modelo físico do banco

## Observação de implementação
No backend atual:

- classe `Usuario` mapeia a tabela `usuario`
- classe `Ubs` mapeia a tabela `ubs`
- classe `Especialidade` mapeia a tabela `especialidade`
- classe `Medico` mapeia a tabela `profissional`
- classe `ProfissionalEspecialidade` mapeia a tabela `profissional_especialidade`
- classe `AgendaSlot` mapeia a tabela `agenda_profissional`
- classe `Consulta` mapeia a tabela `agendamento`

## Tabelas

### `ubs`
Colunas:

- `id` BIGSERIAL PK
- `nome` VARCHAR(150) NOT NULL
- `endereco` VARCHAR(200) NOT NULL
- `cidade` VARCHAR(100) NOT NULL
- `estado` VARCHAR(2) NOT NULL
- `telefone` VARCHAR(20) NOT NULL
- `created_at` TIMESTAMPTZ NOT NULL
- `updated_at` TIMESTAMPTZ NOT NULL

Constraints:

- `pk_ubs`
- `ck_ubs_estado`

### `usuario`
Colunas:

- `id` BIGSERIAL PK
- `nome` VARCHAR(150) NOT NULL
- `cpf` VARCHAR(11) NOT NULL
- `cartao_sus` VARCHAR(15) NOT NULL
- `email` VARCHAR(150) NOT NULL
- `telefone` VARCHAR(20) NOT NULL
- `senha_hash` VARCHAR(255) NOT NULL
- `tipo_usuario` VARCHAR(20) NOT NULL
- `ubs_id` BIGINT NOT NULL
- `created_at` TIMESTAMPTZ NOT NULL
- `updated_at` TIMESTAMPTZ NOT NULL

FKs:

- `usuario.ubs_id` -> `ubs.id`

Constraints:

- `pk_usuario`
- `uk_usuario_cpf`
- `uk_usuario_cartao_sus`
- `uk_usuario_email`
- `fk_usuario_ubs`
- `ck_usuario_cpf`
- `ck_usuario_cartao_sus`
- `ck_usuario_tipo`

### `especialidade`
Colunas:

- `id` BIGSERIAL PK
- `nome` VARCHAR(100) NOT NULL
- `descricao` VARCHAR(255) NULL
- `ativo` BOOLEAN NOT NULL
- `created_at` TIMESTAMPTZ NOT NULL
- `updated_at` TIMESTAMPTZ NOT NULL

Constraints:

- `pk_especialidade`
- `uk_especialidade_nome`

### `profissional`
Colunas:

- `id` BIGSERIAL PK
- `nome` VARCHAR(150) NOT NULL
- `registro_conselho` VARCHAR(30) NOT NULL
- `ubs_id` BIGINT NOT NULL
- `ativo` BOOLEAN NOT NULL
- `created_at` TIMESTAMPTZ NOT NULL
- `updated_at` TIMESTAMPTZ NOT NULL

FKs:

- `profissional.ubs_id` -> `ubs.id`

Constraints:

- `pk_profissional`
- `uk_profissional_registro_conselho`
- `fk_profissional_ubs`

### `profissional_especialidade`
Colunas:

- `id` BIGSERIAL PK
- `profissional_id` BIGINT NOT NULL
- `especialidade_id` BIGINT NOT NULL
- `principal` BOOLEAN NOT NULL
- `created_at` TIMESTAMPTZ NOT NULL
- `updated_at` TIMESTAMPTZ NOT NULL

FKs:

- `profissional_id` -> `profissional.id`
- `especialidade_id` -> `especialidade.id`

Constraints:

- `pk_profissional_especialidade`
- `uk_profissional_especialidade`
- `fk_profissional_especialidade_profissional`
- `fk_profissional_especialidade_especialidade`

### `agenda_profissional`
Colunas:

- `id` BIGSERIAL PK
- `profissional_id` BIGINT NOT NULL
- `especialidade_id` BIGINT NOT NULL
- `data_agenda` DATE NOT NULL
- `hora_inicio` TIME NOT NULL
- `hora_fim` TIME NOT NULL
- `disponivel` BOOLEAN NOT NULL
- `created_at` TIMESTAMPTZ NOT NULL
- `updated_at` TIMESTAMPTZ NOT NULL

FKs:

- `profissional_id` -> `profissional.id`
- `especialidade_id` -> `especialidade.id`

Constraints:

- `pk_agenda_profissional`
- `uk_agenda_profissional_profissional_data_hora`
- `fk_agenda_profissional_profissional`
- `fk_agenda_profissional_especialidade`
- `ck_agenda_profissional_horario`

### `agendamento`
Colunas:

- `id` BIGSERIAL PK
- `usuario_id` BIGINT NOT NULL
- `agenda_profissional_id` BIGINT NOT NULL
- `status` VARCHAR(30) NOT NULL
- `observacoes` VARCHAR(500) NULL
- `criado_em` TIMESTAMPTZ NOT NULL
- `cancelado_em` TIMESTAMPTZ NULL
- `motivo_cancelamento` VARCHAR(255) NULL
- `encaixe_automatico` BOOLEAN NOT NULL

FKs:

- `usuario_id` -> `usuario.id`
- `agenda_profissional_id` -> `agenda_profissional.id`

Constraints:

- `pk_agendamento`
- `fk_agendamento_usuario`
- `fk_agendamento_agenda_profissional`
- `ck_agendamento_status`
- `ck_agendamento_cancelamento`
