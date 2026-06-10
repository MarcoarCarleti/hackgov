# HackGov Sistema Enterprise - Documentação da Fase 4

## Capa

Projeto: HackGov Sistema Enterprise - UBS Inteligente  
Integrantes: [NOME DO INTEGRANTE] - [RM]  
Turma: [TURMA]  
Disciplina: [DISCIPLINA]  
Professor/Tutor: [PROFESSOR/TUTOR]  
Data: Junho de 2026

## Introdução

O HackGov é um MVP GovTech voltado à redução de absenteísmo em consultas da Atenção Básica. O sistema permite cadastro de cidadão, login, consulta de agenda disponível, agendamento, cancelamento, fila de espera, notificações simuladas e painel gerencial.

O objetivo da Fase 4 foi revisar a versão existente, completar o frontend funcional em React, reforçar segurança da informação, demonstrar manipulação de arquivos, refletir sobre blockchain e preparar documentação, slides e roteiro de vídeo.

## Parte 1 - Versão atual do projeto

O projeto já possuía backend Spring Boot, frontend Next.js, banco PostgreSQL, autenticação JWT, perfis de acesso, dashboard gerencial e regras de negócio de agendamento.

Funcionalidades existentes:

- cadastro e login;
- autenticação JWT;
- autorização por perfil;
- agenda disponível por UBS de referência;
- criação e cancelamento de consultas;
- fila de espera;
- reaproveitamento automático de vagas;
- notificações simuladas;
- dashboard com KPIs e gráficos;
- auditoria em eventos sensíveis;
- testes de integração no backend.

Melhorias desta fase:

- auditoria formal do repositório;
- tela de arquivos e relatórios para gestor;
- upload simulado com validação;
- exportação CSV usando API real;
- documentação de segurança;
- backlog atualizado;
- reflexão sobre blockchain;
- documentação final pronta para PDF;
- conteúdo de slides;
- roteiro de vídeo;
- instruções de entrega.

## Parte 2 - Ajustes no backlog

O backlog da Fase 4 foi atualizado em `docs/product-backlog-fase-4.md` com user stories de frontend, segurança, arquivos, API, estados de integração, responsividade e blockchain.

User stories prioritárias:

- dashboard gerencial;
- agendamento pelo cidadão;
- cancelamento com reaproveitamento;
- exportação CSV;
- controle de acesso por perfil.

## Parte 3 - Protótipo funcional com React

O frontend usa Next.js, React 19 e TypeScript.

Telas principais:

- login;
- cadastro;
- início do cidadão;
- agenda;
- minhas consultas;
- notificações;
- dashboard do gestor;
- consultas;
- fila de espera;
- médicos e UBS;
- arquivos e relatórios.

Componentes principais:

- `AppShell`;
- `KpiCard`;
- módulos de API em `frontend/src/lib`.

Validações:

- campos obrigatórios no cadastro e login;
- e-mail válido;
- CPF e Cartão SUS com tamanho esperado;
- senha com tamanho mínimo;
- arquivo com extensão permitida e tamanho máximo.

Consumo de APIs:

- `frontend/src/lib/api.ts` centraliza chamadas REST.
- `frontend/src/lib/agendamentos.ts` concentra operações de consultas.
- `frontend/src/lib/arquivos.ts` concentra upload simulado e CSV.

Estados de integração:

- carregando;
- sucesso;
- erro.

Responsividade:

O layout usa Tailwind CSS com grids responsivos, tabelas com rolagem horizontal e navegação adaptável.

## Parte 4 - Segurança da informação

Práticas aplicadas:

1. Validação de entradas no frontend e backend.
2. Autenticação JWT e autorização por perfil.
3. Senhas com BCrypt.
4. Mascaramento de dados sensíveis em respostas de sessão.
5. Tratamento de erros sem stack trace para o usuário.
6. Prevenção básica contra XSS ao não renderizar HTML do usuário.

Detalhes estão em `docs/seguranca-da-informacao.md`.

## Parte 5 - Blockchain

Blockchain poderia ser usada como camada de evidência imutável para eventos críticos, especialmente alteração de status de consulta e encaixe automático.

O sistema não deve gravar dados pessoais diretamente na blockchain. A recomendação é registrar hash do evento, timestamp, tipo do evento e identificador não sensível.

Referências:

- European Blockchain Services Infrastructure (EBSI)
- OECD, Blockchains Unchained

Detalhes estão em `docs/blockchain.md`.

## Manipulação de arquivos

Funcionalidade implementada:

- upload simulado de documento;
- validação de extensão e tamanho;
- histórico da sessão;
- exportação CSV de consultas.

Tipos aceitos no upload:

- PDF;
- PNG;
- JPG;
- JPEG;
- CSV.

Limitações:

- não há persistência real de arquivo;
- não há antivírus;
- validação final de arquivos deve existir no backend em produção.

## Arquitetura técnica

Visão geral:

- frontend Next.js consome API REST;
- backend Spring Boot expõe endpoints protegidos;
- banco PostgreSQL armazena dados operacionais;
- JPA mapeia entidades;
- JWT protege rotas;
- auditoria registra eventos sensíveis.

Frontend:

- Next.js 16;
- React 19;
- TypeScript;
- Tailwind CSS;
- Recharts.

Backend:

- Spring Boot 3.3.5;
- Java 17;
- Spring Security;
- Spring Data JPA;
- Jakarta Validation;
- JWT;
- BCrypt.

Banco de dados:

- PostgreSQL em runtime;
- H2 em testes.

Integrações:

- REST entre frontend e backend;
- notificações simuladas;
- upload simulado;
- exportação CSV local no navegador.

## Como executar o projeto

Pré-requisitos:

- Java 17 ou superior;
- Node.js 20 ou superior;
- PostgreSQL 14 ou superior.

Variáveis de ambiente do backend:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/hackgov"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="postgres"
$env:APP_JWT_SECRET="altere-este-segredo-em-ambiente-real-com-tamanho-seguro"
```

Executar backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Executar frontend:

```powershell
cd frontend
npm install
npm run dev
```

Arquivo opcional do frontend:

```bash
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Acesso:

- frontend: `http://localhost:3000`
- backend: `http://localhost:8080`

Credenciais de demonstração documentadas no README:

- Admin: `admin@hackgov.local` / `123456`
- Gestor: `gestor@hackgov.local` / `123456`
- Cidadão: `cidadao@hackgov.local` / `123456`

## Conclusão

A Fase 4 consolidou o HackGov como MVP fullstack funcional, com frontend React, consumo de API, validações, estados de integração, práticas de segurança, manipulação de arquivos simulada, reflexão de blockchain e materiais de entrega.

Limitações:

- upload ainda é simulado;
- blockchain não foi implementada;
- notificações externas são simuladas;
- execução local depende de PostgreSQL e variáveis de ambiente.

Próximos passos:

- persistir documentos em storage seguro;
- integrar notificações reais;
- ampliar acessibilidade;
- criar trilha de auditoria exportável;
- avaliar prova de conceito com blockchain permissionada.
