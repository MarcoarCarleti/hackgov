# Resumo da entrega FIAP

## Evolução do projeto
O HackGov já possuía um fluxo de consultas, mas a modelagem ainda trazia redundâncias e uma especialidade armazenada como texto no profissional. A evolução aplicada reorganizou o banco para um modelo mais normalizado e aderente ao cenário de agendamento em UBS.

## O que foi atualizado
- modelagem lógica e física do banco
- script PostgreSQL com tabelas e constraints
- entidades JPA alinhadas às tabelas `usuario`, `ubs`, `especialidade`, `profissional`, `profissional_especialidade`, `agenda_profissional` e `agendamento`
- repositories básicos das novas entidades
- backend ajustado para usar a agenda como origem dos dados do agendamento
- frontend ajustado para consumir agendamentos por meio de um service dedicado

## Descrição do banco
O banco ficou organizado em torno de sete entidades centrais:

- `usuario` para autenticação e vínculo do paciente à UBS
- `ubs` para a unidade de saúde
- `especialidade` para o catálogo médico
- `profissional` para os profissionais da UBS
- `profissional_especialidade` para a relação N:N entre profissional e especialidade
- `agenda_profissional` para os slots disponíveis
- `agendamento` para as consultas marcadas pelos pacientes

## Justificativa da normalização
A normalização reduz redundância e evita inconsistências. A especialidade deixou de ficar em texto dentro do profissional, e o agendamento deixou de repetir profissional, UBS, data e hora, passando a depender do slot de agenda. Isso melhora manutenção, integridade e reutilização do modelo.

## Como executar o SQL
Exemplo com `psql`:

```bash
psql -U postgres -d hackgov -f docs/banco/create_tables.sql
```

Depois da criação das tabelas, o backend pode ser iniciado normalmente para utilizar a base já estruturada.
