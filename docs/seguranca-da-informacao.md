# Segurança da Informação - Fase 4

## Práticas aplicadas

### 1. Validação de entradas

Onde foi aplicada:

- Backend:
  - `RegisterRequest`
  - `CriarConsultaRequest`
  - `CancelarConsultaRequest`
  - `CriarFilaEsperaRequest`
  - `AtualizarStatusConsultaRequest`
- Frontend:
  - `frontend/src/app/cadastro/page.tsx`
  - `frontend/src/app/login/page.tsx`
  - `frontend/src/app/gestor/arquivos/page.tsx`
  - `frontend/src/lib/arquivos.ts`

Justificativa:

O sistema recebe dados pessoais, agendamentos e documentos operacionais. Por isso, campos obrigatórios, CPF, Cartão SUS, e-mail, senha e arquivos precisam ser validados antes do processamento.

Exemplos:

- CPF com 11 dígitos no cadastro.
- Cartão SUS com 15 dígitos no cadastro.
- E-mail com formato válido.
- Senha com tamanho mínimo.
- Arquivo com extensão permitida e tamanho máximo de 5 MB.

Limitação do MVP:

A validação de arquivos é feita no frontend e o upload é simulado. Em produção, o backend também deve validar MIME type, extensão, tamanho, antivírus e regras de armazenamento.

### 2. Controle de acesso por perfil

Onde foi aplicado:

- Backend:
  - `SecurityConfig`
  - `CidadaoController`
  - `GestorController`
  - `JobController`
- Frontend:
  - `frontend/src/lib/useAuthGuard.ts`
  - rotas do cidadão e do gestor

Perfis usados:

- `PACIENTE`
- `GESTOR`
- `ADMIN`

Justificativa:

As ações de cidadão e gestor têm responsabilidades diferentes. O cidadão agenda e cancela suas consultas. O gestor acompanha indicadores, altera status, consulta filas e exporta relatórios.

Exemplos:

- `GET /dashboard/resumo` exige `GESTOR` ou `ADMIN`.
- `POST /consultas` exige `PACIENTE`.
- `/gestor/arquivos` usa guarda de autenticação para `GESTOR` e `ADMIN`.

Limitação do MVP:

O frontend oculta rotas indevidas, mas a proteção efetiva fica no backend por `@PreAuthorize`.

### 3. Proteção de dados sensíveis

Onde foi aplicada:

- `MaskingUtils`
- `AuthService`
- endpoint `/auth/me`
- documentação de blockchain
- exportação CSV sem CPF, Cartão SUS, senha ou endereço do paciente

Justificativa:

O projeto trata dados de saúde e identificação. Dados como CPF, Cartão SUS e senha não devem ser expostos quando não forem necessários para a operação.

Exemplos:

- Senha armazenada com BCrypt.
- CPF e Cartão SUS mascarados em respostas de sessão.
- Relatório CSV de consultas não inclui CPF, Cartão SUS nem senha.

Limitação do MVP:

O relatório ainda exibe nome do paciente. Em produção, perfis e finalidade de uso devem determinar se o nome pode aparecer ou se deve ser pseudonimizado.

### 4. Tratamento de erros seguro

Onde foi aplicado:

- `GlobalExceptionHandler`
- `frontend/src/lib/api.ts`
- telas do frontend

Justificativa:

O usuário deve receber mensagens claras, sem stack trace, SQL, nomes internos de classes ou detalhes de infraestrutura.

Exemplos:

- `INTERNAL_ERROR` retorna "Erro interno".
- Validações retornam "Dados inválidos".
- Frontend exibe mensagens amigáveis de falha.

Limitação do MVP:

Logs técnicos de backend não foram revisados nesta fase. Em produção, logs devem ser centralizados e higienizados para evitar exposição de dados sensíveis.

### 5. Prevenção básica contra XSS

Onde foi aplicada:

- React/Next.js renderizam valores como texto por padrão.
- O projeto não usa `dangerouslySetInnerHTML`.
- Entradas são tratadas como strings comuns.
- A tela de arquivos não renderiza HTML vindo de arquivo ou usuário.

Justificativa:

Evitar renderização de HTML não confiável reduz risco de execução de script injetado.

Limitação do MVP:

Caso o projeto futuramente aceite conteúdo rico ou pré-visualização de arquivos, será necessário sanitizador específico e política de Content Security Policy.
