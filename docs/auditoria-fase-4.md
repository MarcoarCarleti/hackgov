# Auditoria da Fase 4 - HackGov Sistema Enterprise

## 1. Estrutura atual do projeto

### Backend existente

- Pasta: `backend/`
- Aplicação Spring Boot em `backend/src/main/java/br/gov/hackgov`
- Camadas encontradas:
  - `web`: controllers REST e tratamento global de exceções
  - `service`: regras de negócio, dashboard, auditoria, fila de espera, agenda e autenticação
  - `repository`: Spring Data JPA
  - `domain`: entidades JPA e enums
  - `security`: JWT, filtro de autenticação e usuário autenticado
  - `seed`: carga inicial de dados
- Testes de integração em `backend/src/test/java/br/gov/hackgov/integration`

### Frontend existente

- Pasta: `frontend/`
- Aplicação Next.js com React e TypeScript
- Rotas principais:
  - `/login`
  - `/cadastro`
  - `/cidadao`
  - `/cidadao/agenda`
  - `/cidadao/minhas-consultas`
  - `/cidadao/notificacoes`
  - `/gestor/dashboard`
  - `/gestor/consultas`
  - `/gestor/fila-espera`
  - `/gestor/medicos-agendas`
  - `/gestor/arquivos`
- Componentes reutilizáveis encontrados:
  - `AppShell`
  - `KpiCard`
- Clientes de integração:
  - `frontend/src/lib/api.ts`
  - `frontend/src/lib/agendamentos.ts`
  - `frontend/src/lib/arquivos.ts`

### Banco de dados

- Banco previsto: PostgreSQL
- Persistência por Spring Data JPA
- Entidades principais:
  - `Usuario`
  - `Ubs`
  - `Medico`
  - `AgendaSlot`
  - `Consulta`
  - `FilaEspera`
  - `Notificacao`
  - `LogEvento`
  - `Feriado`
- Documentação SQL existente em `docs/banco/`
- `application.yml` usa variáveis de ambiente para conexão e segredo JWT.

### Documentação já existente

- `README.md`
- `docs/entrega-fiape-resumo.md`
- `docs/banco/create_tables.sql`
- `docs/banco/modelo-fisico.md`
- `docs/banco/modelo-logico.md`
- `docs/banco/normalizacao.md`

### Arquivos de configuração

- `Dockerfile`
- `backend/pom.xml`
- `backend/src/main/resources/application.yml`
- `backend/src/test/resources/application.yml`
- `frontend/package.json`
- `frontend/tsconfig.json`
- `frontend/next.config.ts`
- `frontend/eslint.config.mjs`
- `frontend/postcss.config.mjs`
- `.editorconfig`
- `.gitignore`

### Scripts de execução

Backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Frontend:

```powershell
cd frontend
npm install
npm run dev
```

Build frontend:

```powershell
cd frontend
npm run build
```

Testes backend:

```powershell
cd backend
.\mvnw.cmd test
```

## 2. Tecnologias encontradas

- Linguagem backend: Java 17
- Framework backend: Spring Boot 3.3.5
- Segurança backend: Spring Security, JWT, BCrypt, autorização por perfil
- Validação backend: Jakarta Validation
- Banco: PostgreSQL em runtime e H2 para testes
- ORM: Spring Data JPA e Hibernate
- Frontend: Next.js 16, React 19, TypeScript
- Estilo: Tailwind CSS 4
- Gráficos: Recharts
- Gerenciadores de pacotes: Maven Wrapper e npm
- Build: Maven, Next.js
- Container: Dockerfile para build do backend

## 3. O que já atende ao enunciado

| Requisito | Situação | Evidência |
| --- | --- | --- |
| Backend | Atendido | Spring Boot com controllers, services, repositories e testes |
| Banco de dados | Atendido | Entidades JPA, PostgreSQL e documentação SQL |
| APIs | Atendido | Endpoints REST para autenticação, consultas, dashboard, fila, UBS, médicos e jobs |
| Autenticação | Atendido | Login, cadastro, JWT, filtro de autenticação e sessão no frontend |
| Autorização | Atendido | Perfis `PACIENTE`, `GESTOR` e `ADMIN` com `@PreAuthorize` |
| Validações | Parcialmente atendido | Validação no backend por DTOs e validação básica no frontend |
| Segurança | Atendido | JWT, BCrypt, autorização por perfil, CORS controlado, mascaramento e erros seguros |
| Manipulação de arquivos | Atendido nesta fase | Tela `/gestor/arquivos` com upload simulado validado e exportação CSV |
| React | Atendido | Frontend Next.js com React e TypeScript |
| Estados de integração | Atendido | Telas exibem carregando, sucesso e erro nas operações principais |
| Documentação | Parcialmente atendido antes, atendido nesta fase | Novos documentos da Fase 4 criados em `docs/` |
| Backlog | Atendido nesta fase | `docs/product-backlog-fase-4.md` |
| Slides | Atendido nesta fase | `docs/slides-hackgov-fase-4.md` |
| Vídeo | Atendido nesta fase | `docs/roteiro-video-5min.md` |
| Blockchain | Atendido nesta fase | `docs/blockchain.md` |

## 4. Classificação por requisito da Fase 4

| Requisito da atividade | Classificação |
| --- | --- |
| Relatar versão atual e evoluções | Atendido |
| Ajustar Product Backlog | Atendido |
| Criar ou completar protótipo React | Atendido |
| Aplicar três práticas de segurança | Atendido |
| Refletir sobre blockchain | Atendido |
| Gerar documentação do projeto | Atendido |
| Gerar conteúdo para até 10 slides | Atendido |
| Preparar instruções de entrega | Atendido |
| Preparar roteiro para vídeo de até 5 minutos | Atendido |
| Executar ou verificar comandos | Atendido |

## 5. Riscos identificados

- O `README.md` cita configuração padrão de banco, mas `backend/src/main/resources/application.yml` depende de variáveis de ambiente obrigatórias.
- O backend exige PostgreSQL disponível para execução normal.
- O frontend depende de `NEXT_PUBLIC_API_URL` quando a API não estiver em `http://localhost:8080`.
- A funcionalidade de upload de arquivo é simulada no frontend e não persiste documentos no backend.
- A exportação CSV depende do endpoint protegido `GET /consultas`, portanto exige login como `GESTOR` ou `ADMIN`.
- Não há integração real com SMS, e-mail, push ou blockchain.
- O armazenamento da sessão no frontend usa `localStorage`, adequado ao MVP acadêmico, mas exige revisão para produção.
- Há documentação anterior com nomes de tabelas e contexto que pode divergir da versão atual das entidades.

## 6. Plano de ação executado

1. Preservar o backend existente, sem alterar regras transacionais.
2. Preservar o frontend Next.js existente.
3. Adicionar tela protegida `/gestor/arquivos`.
4. Adicionar módulo `frontend/src/lib/arquivos.ts`.
5. Implementar validação de extensão, tamanho e nome de arquivo.
6. Simular upload com estados de carregamento, sucesso e erro.
7. Implementar exportação CSV de consultas usando API real protegida.
8. Atualizar navegação do painel do gestor.
9. Criar documentação de segurança, arquivos, backlog, blockchain, slides, roteiro de vídeo e entrega.
10. Executar validações finais de build/testes quando as dependências locais estiverem disponíveis.

## 7. Validação final local

- Frontend: `npm run build` executado com sucesso em `frontend/`.
- Frontend: `npm run lint` executado com sucesso em `frontend/`.
- Backend: `.\mvnw.cmd test` executado com sucesso em `backend/`.
- Resultado dos testes backend: 9 testes executados, 0 falhas, 0 erros.
- Servidor frontend iniciado em `http://localhost:3000`.
