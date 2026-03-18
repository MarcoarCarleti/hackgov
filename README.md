# HackGov - UBS Inteligente (MVP)

MVP fullstack para redução de absenteísmo em consultas da Atenção Básica, com foco em cancelamento fácil, reaproveitamento automático de vagas e visibilidade gerencial por dashboard.

## Problema resolvido

Quando o cidadão falta sem aviso, a UBS perde capacidade de atendimento e aumenta fila de espera. Este projeto transforma o fluxo em um serviço digital com:

- agendamento online por UBS de referência;
- cancelamento com antecedência mínima (12h);
- reaproveitamento automático de vaga por fila de espera;
- alertas automáticos (48h/24h) simulados e auditáveis;
- dashboard operacional para gestor.

## Arquitetura

- `backend/`: Spring Boot 3 + Java 17 + Spring Security JWT + Spring Data JPA (PostgreSQL)
- `frontend/`: Next.js 16 + TypeScript + Tailwind + Recharts
- Comunicação: REST (`frontend -> backend`)
- Persistência: PostgreSQL
- Seed: carga automática no backend (quando banco vazio)

### Decisões técnicas principais

1. **Cancelamento + encaixe automático no backend (transacional)**
- Ao cancelar consulta válida (>12h), o slot é liberado e a fila de espera é processada no mesmo fluxo.
- Se houver paciente elegível, o sistema cria consulta `ENCAIXADA` imediatamente e registra auditoria.

2. **Regras críticas centralizadas em `ConsultaService`**
- bloqueio de data passada;
- bloqueio de feriado;
- bloqueio de conflito horário do mesmo paciente;
- restrição à UBS de referência.

3. **Segurança e LGPD (mínimo viável)**
- autenticação JWT;
- autorização por perfil (`CIDADAO`, `GESTOR`, `ADMIN`);
- senha com hash BCrypt;
- mascaramento de CPF/Cartão SUS em `/auth/me`;
- logs de auditoria (`LogEvento`) para eventos sensíveis.

## Estrutura de pastas

- `backend/src/main/java/br/gov/hackgov/...`
- `backend/src/test/java/br/gov/hackgov/integration/...`
- `frontend/src/app/...`
- `frontend/src/components/...`
- `frontend/src/lib/...`

## Funcionalidades implementadas

### Cidadão
- cadastro com CPF e Cartão SUS (`/auth/register`)
- login (`/auth/login`)
- agenda disponível da UBS de referência (`/agenda/disponivel`)
- agendamento (`POST /consultas`)
- minhas consultas (`GET /me/consultas`)
- cancelamento com confirmação na UI (`POST /consultas/{id}/cancelar`)
- notificações simuladas (`GET /notificacoes`)
- entrada em fila de espera (`POST /fila-espera`)

### Gestor/Admin
- dashboard com KPIs e gráficos:
  - total agendamentos
  - realizadas
  - faltas e taxa de faltas
  - cancelamentos e taxa de cancelamentos
  - vagas reaproveitadas
  - ocupação por dia
  - série agendamentos x faltas
  - ranking de médicos
- visão de consultas e atualização de status
- visão de fila de espera
- listagem de médicos e UBS

### Jobs do sistema
- `POST /jobs/processar-alertas`
- `POST /jobs/processar-encaixes`
- agendamento automático por hora no backend (scheduler)

## Modelagem de dados (resumo)

Entidades implementadas:

- `Usuario`
- `Ubs`
- `Medico`
- `AgendaSlot`
- `Consulta`
- `FilaEspera`
- `Notificacao`
- `LogEvento`
- `Feriado`

Status de consulta implementados:

- `AGENDADA`
- `CANCELADA_PELO_PACIENTE`
- `CANCELADA_PELO_SISTEMA`
- `REAGENDADA`
- `REALIZADA`
- `FALTA`
- `ENCAIXADA`

## Seed (dados fake)

Ao subir o backend com banco vazio, o seed cria:

- UBSs, médicos, cidadãos, gestor e admin
- slots de agenda
- **~500 consultas** com distribuição de status
- fila de espera ativa
- notificações simuladas
- feriados

Cenário de demonstração aproximado:

- 500 agendamentos
- ~30% faltas
- ~10% cancelamentos prévios
- consultas encaixadas automaticamente

## Credenciais de demonstração

- Admin: `admin@hackgov.local` / `123456`
- Gestor: `gestor@hackgov.local` / `123456`
- Cidadão: `cidadao@hackgov.local` / `123456`

## Pré-requisitos

- Java 17+
- Node.js 20+
- PostgreSQL 14+

Opcional (Docker para banco):

```bash
docker run --name hackgov-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=hackgov \
  -p 5432:5432 -d postgres:16
```

## Como executar

### 1) Backend

```bash
cd backend
./mvnw spring-boot:run
```

No Windows (PowerShell):

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Config padrão em `backend/src/main/resources/application.yml`:

- URL: `jdbc:postgresql://localhost:5432/hackgov`
- user: `postgres`
- password: `postgres`
- porta API: `8080`

### 2) Frontend

```bash
cd frontend
npm install
npm run dev
```

Crie `frontend/.env.local` (opcional):

```bash
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Acesso web: `http://localhost:3000`

## Testes

Testes implementados (backend):

1. cadastro duplicado falha
2. CPF inválido falha
3. agendamento em data passada falha
4. agendamento em feriado falha
5. cancelamento com menos de 12h falha
6. cancelamento válido libera vaga
7. encaixe automático ocupa vaga liberada
8. endpoint de gestor exige perfil correto

Execução:

```bash
cd backend
./mvnw test
```

## Endpoints principais

### Auth
- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`

### Cidadão
- `GET /me/consultas`
- `POST /consultas`
- `POST /consultas/{id}/cancelar`
- `GET /agenda/disponivel`
- `POST /fila-espera`
- `GET /notificacoes`

### Gestor/Admin
- `GET /dashboard/resumo`
- `GET /dashboard/agendamentos`
- `GET /dashboard/faltas`
- `GET /dashboard/cancelamentos`
- `GET /dashboard/reaproveitamento`
- `GET /dashboard/ranking-medicos`
- `GET /dashboard/serie-agendamentos-faltas`
- `GET /consultas`
- `PATCH /consultas/{id}/status`
- `GET /medicos`
- `GET /ubs`
- `GET /fila-espera`

### Jobs
- `POST /jobs/processar-alertas`
- `POST /jobs/processar-encaixes`

## Fluxos-chave implementados

1. **Cadastro**: valida CPF + unicidade de CPF/Cartão SUS.
2. **Agendamento**: mostra slot livre e valida regras (passado/feriado/conflito/UBS).
3. **Cancelamento**: bloqueia <12h, cancela, libera slot e aciona encaixe automático.
4. **Alertas**: lembretes 48h/24h simulados e registrados.
5. **Dashboard**: KPIs e gráficos por período com filtros de UBS/médico.

## Riscos e mitigações incorporadas

- **Exclusão digital**: UI simples, mobile-first, fluxo enxuto.
- **Vazamento de dados**: JWT, perfis, hash de senha, auditoria e mascaramento de dados sensíveis.

## Melhorias futuras mapeadas

- integração real com SMS/e-mail/push
- integração com sistemas SUS/municipais
- confirmação ativa de presença
- omnichannel (telefone/totem/presencial integrado)
- acessibilidade avançada
- analytics preditivo para absenteísmo
- priorização inteligente da fila de encaixe
