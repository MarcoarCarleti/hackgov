# Normalização do banco

## 1FN
A Primeira Forma Normal foi aplicada garantindo que:

- cada tabela possui chave primária própria
- cada coluna armazena apenas um valor atômico
- não existem listas ou conjuntos em uma única coluna

Exemplos no projeto:

- `especialidade.nome` armazena apenas um nome de especialidade por linha
- `agenda_profissional.hora_inicio` e `hora_fim` ficam em colunas separadas
- a relação entre profissional e especialidade não fica em texto concatenado, mas sim na tabela `profissional_especialidade`

## 2FN
A Segunda Forma Normal foi aplicada removendo dependências parciais em estruturas associativas.

Exemplo principal:

- a relação N:N entre `profissional` e `especialidade` foi isolada em `profissional_especialidade`
- os atributos da associação, como `principal`, dependem da combinação entre profissional e especialidade, e por isso pertencem à tabela associativa

Na agenda:

- os dados do slot dependem do próprio identificador da agenda
- não foi mantido campo duplicado de UBS dentro do slot, porque essa informação já depende do profissional

## 3FN
A Terceira Forma Normal foi aplicada removendo dependências transitivas e atributos redundantes.

A principal mudança foi em `agendamento`:

- antes era comum repetir profissional, UBS, data e hora dentro da consulta
- agora o agendamento depende do slot de `agenda_profissional`
- profissional, especialidade, data, hora e UBS são obtidos pelo relacionamento, e não por duplicação física

Outros pontos:

- `especialidade` deixou de ser texto dentro de `profissional`
- `usuario` referencia a `ubs` por chave estrangeira, em vez de repetir dados da unidade

## Anomalias evitadas
Com a normalização, foram evitadas as seguintes anomalias:

- anomalia de atualização:
  se o nome de uma especialidade mudar, a alteração ocorre em um único lugar
- anomalia de inserção:
  um profissional pode ser cadastrado e depois receber especialidades pela tabela associativa
- anomalia de exclusão:
  excluir um agendamento não remove a definição da agenda nem a especialidade do profissional
- inconsistência de horário:
  os dados do horário ficam concentrados em `agenda_profissional`

## Justificativa das decisões
As decisões de modelagem foram tomadas para equilibrar três pontos:

1. aderência acadêmica à atividade FIAP
2. consistência com o backend Spring Boot já existente
3. redução de redundância no banco

Por isso:

- a nomenclatura física foi alinhada com `profissional`, `agenda_profissional` e `agendamento`
- as classes Java legadas foram preservadas para não quebrar o projeto
- o frontend continuou consumindo o mesmo formato de resposta, mas agora apoiado em uma base mais normalizada
