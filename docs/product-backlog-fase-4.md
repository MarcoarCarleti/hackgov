# Product Backlog - Fase 4

## User Story 1

Como gestor da UBS,  
quero visualizar um dashboard com indicadores de agendamentos, faltas, cancelamentos e reaproveitamento,  
para acompanhar a eficiência operacional da unidade.

Prioridade: Alta

Critérios de aceitação:

- Dado que estou autenticado como `GESTOR` ou `ADMIN`,
- Quando acessar o dashboard,
- Então devo ver KPIs e gráficos carregados a partir da API.
- Dado que a API falhe,
- Quando a tela tentar carregar os dados,
- Então devo ver uma mensagem de erro amigável.

## User Story 2

Como cidadão,  
quero consultar horários disponíveis e agendar uma consulta,  
para acessar o serviço da UBS sem atendimento presencial inicial.

Prioridade: Alta

Critérios de aceitação:

- Dado que estou autenticado como `PACIENTE`,
- Quando acessar a agenda,
- Então devo visualizar slots disponíveis da minha UBS de referência.
- Dado que escolhi um slot válido,
- Quando confirmar o agendamento,
- Então a consulta deve ser criada e uma mensagem de sucesso deve aparecer.

## User Story 3

Como cidadão,  
quero cancelar uma consulta dentro das regras do sistema,  
para liberar a vaga e permitir reaproveitamento pela fila de espera.

Prioridade: Alta

Critérios de aceitação:

- Dado que tenho consulta agendada,
- Quando solicitar cancelamento com antecedência permitida,
- Então a consulta deve mudar de status e a vaga deve ser liberada.
- Dado que a consulta esteja a menos de 12 horas,
- Quando tentar cancelar,
- Então o sistema deve bloquear a ação com mensagem clara.

## User Story 4

Como gestor,  
quero exportar um relatório CSV de consultas,  
para analisar dados operacionais fora do sistema.

Prioridade: Alta

Critérios de aceitação:

- Dado que estou autenticado como `GESTOR` ou `ADMIN`,
- Quando clicar em "Baixar CSV de consultas",
- Então o sistema deve consumir a API protegida e gerar um arquivo CSV.
- Dado que a API retorne erro,
- Quando a exportação falhar,
- Então devo ver uma mensagem amigável sem detalhes técnicos.

## User Story 5

Como gestor,  
quero enviar documentos operacionais com validação de formato e tamanho,  
para preparar a futura digitalização de anexos do processo.

Prioridade: Média

Critérios de aceitação:

- Dado que selecionei arquivo PDF, PNG, JPG, JPEG ou CSV de até 5 MB,
- Quando enviar o documento,
- Então o sistema deve simular o envio e registrar o histórico da sessão.
- Dado que o arquivo esteja fora do padrão,
- Quando tentar enviar,
- Então o sistema deve bloquear a ação e informar o motivo.

## User Story 6

Como administrador,  
quero que rotas sensíveis respeitem perfis de acesso,  
para reduzir risco de operação indevida por usuários sem permissão.

Prioridade: Alta

Critérios de aceitação:

- Dado que estou autenticado como `PACIENTE`,
- Quando tentar acessar uma rota de gestor,
- Então o acesso deve ser bloqueado.
- Dado que estou autenticado como `GESTOR` ou `ADMIN`,
- Quando acessar rotas gerenciais,
- Então devo conseguir usar as operações autorizadas.

## User Story 7

Como usuário do sistema,  
quero receber feedback de carregamento, sucesso e erro nas operações,  
para entender o estado da integração com a API.

Prioridade: Média

Critérios de aceitação:

- Dado que uma operação esteja em andamento,
- Quando a requisição ainda não terminou,
- Então devo ver texto ou botão indicando carregamento.
- Dado que a operação termine com sucesso,
- Quando o sistema atualizar a tela,
- Então devo ver mensagem de confirmação.
- Dado que a operação falhe,
- Quando a API retornar erro,
- Então devo ver mensagem amigável.

## User Story 8

Como equipe do projeto,  
quero registrar evidências críticas de auditoria com possibilidade futura de blockchain,  
para aumentar rastreabilidade de eventos sensíveis sem expor dados pessoais.

Prioridade: Média

Critérios de aceitação:

- Dado que um evento crítico ocorra, como alteração de status de consulta,
- Quando a auditoria for registrada,
- Então o sistema deve manter dados suficientes para rastreabilidade.
- Dado que uma futura blockchain seja adotada,
- Quando gerar evidência imutável,
- Então apenas hash, timestamp e identificador não sensível devem ser registrados.
