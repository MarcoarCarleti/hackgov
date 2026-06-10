# Manipulação de Arquivos - Fase 4

## Funcionalidades implementadas

Foi criada a tela:

- `frontend/src/app/gestor/arquivos/page.tsx`

A tela está disponível para perfis `GESTOR` e `ADMIN` e possui duas operações:

1. Upload simulado de documento.
2. Exportação de relatório CSV de consultas.

## Upload simulado de documento

O upload é simulado no frontend pelo módulo:

- `frontend/src/lib/arquivos.ts`

Validações aplicadas:

- arquivo obrigatório;
- nome com até 120 caracteres;
- tamanho máximo de 5 MB;
- extensões permitidas:
  - `.pdf`
  - `.png`
  - `.jpg`
  - `.jpeg`
  - `.csv`

Estados tratados:

- carregando: "Enviando documento..."
- sucesso: "Documento validado e enviado com sucesso."
- erro: mensagem amigável de validação ou falha simulada

Estratégia de simulação:

- A função `enviarDocumentoSimulado` usa `Promise` e `setTimeout`.
- Arquivos cujo nome contenha `falha` simulam erro de envio.
- O histórico fica apenas na sessão do navegador.

## Exportação de relatório CSV

A exportação usa API real protegida:

- `GET /consultas`

Fluxo:

1. O gestor acessa `/gestor/arquivos`.
2. O frontend chama `listarAgendamentos`.
3. As consultas retornadas são convertidas para CSV por `gerarCsvConsultas`.
4. O navegador baixa `relatorio-consultas-hackgov.csv`.

Campos exportados:

- id
- data
- hora
- paciente
- profissional
- especialidade
- UBS
- status
- indicador de encaixe automático

Dados não exportados:

- CPF
- Cartão SUS
- senha
- token
- endereço do paciente
- dados técnicos de autenticação

## Limitações

- O upload não persiste arquivo no backend.
- Não há antivírus, storage externo ou assinatura digital.
- A validação de tipo é básica e deve ser repetida no backend em produção.
- O CSV contém nome do paciente para operação do gestor; em produção, a exposição deve seguir finalidade, perfil e LGPD.
