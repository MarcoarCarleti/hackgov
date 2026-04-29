# Modelo lógico do banco

## Visão geral
O recorte de banco da atividade foi organizado em sete entidades centrais do fluxo de agendamento da UBS:

1. `usuario`
2. `ubs`
3. `especialidade`
4. `profissional`
5. `profissional_especialidade`
6. `agenda_profissional`
7. `agendamento`

No backend atual, as tabelas `profissional`, `agenda_profissional` e `agendamento` são representadas pelas classes Java legadas `Medico`, `AgendaSlot` e `Consulta`, preservando o padrão existente do projeto sem perder aderência ao modelo FIAP.

## Entidades, atributos e relacionamentos

### `ubs`
- `id`
- `nome`
- `endereco`
- `cidade`
- `estado`
- `telefone`
- `created_at`
- `updated_at`

Relacionamentos:
- Uma `ubs` possui vários `usuario`.
- Uma `ubs` possui vários `profissional`.

Cardinalidades:
- `ubs` 1:N `usuario`
- `ubs` 1:N `profissional`

### `usuario`
- `id`
- `nome`
- `cpf`
- `cartao_sus`
- `email`
- `telefone`
- `senha_hash`
- `tipo_usuario`
- `ubs_id`
- `created_at`
- `updated_at`

Relacionamentos:
- Cada `usuario` pertence a uma `ubs` de referência.
- Um `usuario` pode realizar vários `agendamento`.

Cardinalidades:
- `usuario` N:1 `ubs`
- `usuario` 1:N `agendamento`

### `especialidade`
- `id`
- `nome`
- `descricao`
- `ativo`
- `created_at`
- `updated_at`

Relacionamentos:
- Uma `especialidade` pode estar associada a vários `profissional`.
- Uma `especialidade` pode aparecer em várias `agenda_profissional`.

Cardinalidades:
- `especialidade` N:N `profissional` via `profissional_especialidade`
- `especialidade` 1:N `agenda_profissional`

### `profissional`
- `id`
- `nome`
- `registro_conselho`
- `ubs_id`
- `ativo`
- `created_at`
- `updated_at`

Relacionamentos:
- Cada `profissional` pertence a uma `ubs`.
- Cada `profissional` pode ter uma ou mais `especialidade`.
- Cada `profissional` pode possuir vários registros em `agenda_profissional`.

Cardinalidades:
- `profissional` N:1 `ubs`
- `profissional` N:N `especialidade` via `profissional_especialidade`
- `profissional` 1:N `agenda_profissional`

### `profissional_especialidade`
- `id`
- `profissional_id`
- `especialidade_id`
- `principal`
- `created_at`
- `updated_at`

Relacionamentos:
- Resolve a relação muitos-para-muitos entre `profissional` e `especialidade`.

Cardinalidades:
- `profissional_especialidade` N:1 `profissional`
- `profissional_especialidade` N:1 `especialidade`

### `agenda_profissional`
- `id`
- `profissional_id`
- `especialidade_id`
- `data_agenda`
- `hora_inicio`
- `hora_fim`
- `disponivel`
- `created_at`
- `updated_at`

Relacionamentos:
- Cada slot de agenda pertence a um `profissional`.
- Cada slot de agenda é vinculado a uma `especialidade`.
- Cada slot pode gerar zero ou mais `agendamento` ao longo do ciclo de vida, mas somente um agendamento ativo por horário.

Cardinalidades:
- `agenda_profissional` N:1 `profissional`
- `agenda_profissional` N:1 `especialidade`
- `agenda_profissional` 1:N `agendamento`

### `agendamento`
- `id`
- `usuario_id`
- `agenda_profissional_id`
- `status`
- `observacoes`
- `criado_em`
- `cancelado_em`
- `motivo_cancelamento`
- `encaixe_automatico`

Relacionamentos:
- Cada `agendamento` pertence a um `usuario`.
- Cada `agendamento` referencia um slot de `agenda_profissional`.

Cardinalidades:
- `agendamento` N:1 `usuario`
- `agendamento` N:1 `agenda_profissional`

## Evolução do projeto
Na versão anterior do projeto, o domínio de consultas já existia, mas ainda havia pontos de desnormalização, principalmente:

- especialidade armazenada como texto em `medico`
- redundância de dados de profissional, UBS, data e hora em `consulta`
- nomenclatura técnica diferente da entrega FIAP

A evolução aplicada foi:

- especialidade passou a ser entidade própria
- foi criada a associação `profissional_especialidade`
- a agenda passou a apontar para `profissional` e `especialidade`
- o agendamento passou a depender do slot de agenda, reduzindo redundância
- o perfil de paciente foi padronizado como `PACIENTE` no backend e frontend

Com isso, o modelo lógico ficou mais consistente com a operação da UBS e com a implementação real do sistema.
