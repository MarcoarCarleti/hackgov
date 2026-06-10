# Entrega HackGov - Fase 4

## Itens para incluir no ZIP

- Projeto completo com `backend/`, `frontend/`, `docs/`, `README.md`, `Dockerfile` e arquivos de configuração.
- Documentação final convertida para PDF a partir de `docs/documentacao-hackgov-fase-4.md`.
- Slides convertidos para PDF a partir de `docs/slides-hackgov-fase-4.md`.
- Documentos auxiliares:
  - `docs/auditoria-fase-4.md`
  - `docs/product-backlog-fase-4.md`
  - `docs/seguranca-da-informacao.md`
  - `docs/manipulacao-arquivos.md`
  - `docs/blockchain.md`
  - `docs/roteiro-video-5min.md`
- Assets, imagens e ícones usados pelo frontend.
- Link do GitHub, se a entrega for por repositório.
- Link do vídeo no YouTube como não listado.
- Arquivo `links-entrega.txt` preenchido.

## Pré-requisitos

- Java 17+
- Node.js 20+
- PostgreSQL 14+
- npm

## Variáveis de ambiente do backend

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/hackgov"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="postgres"
$env:APP_JWT_SECRET="altere-este-segredo-em-ambiente-real-com-tamanho-seguro"
```

## Como executar o backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

## Como executar o frontend

```powershell
cd frontend
npm install
npm run dev
```

Opcional em `frontend/.env.local`:

```bash
NEXT_PUBLIC_API_URL=http://localhost:8080
```

## Links de acesso

- Frontend local: `http://localhost:3000`
- Backend local: `http://localhost:8080`

## Credenciais de demonstração

- Admin: `admin@hackgov.local` / `123456`
- Gestor: `gestor@hackgov.local` / `123456`
- Cidadão: `cidadao@hackgov.local` / `123456`

## Observações

- O upload de documentos é simulado no frontend.
- A exportação CSV consome a API real `GET /consultas`.
- Blockchain foi tratada como proposta arquitetural, não como implementação.
- O vídeo deve demonstrar aplicação funcionando e usar os slides como apoio.
