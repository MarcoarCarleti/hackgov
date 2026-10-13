# HackGov - Fase 5 | IA, SQL e estruturas de dados

## Alterações desta branch
- Motor de classificação categórica Naive Bayes (Laplace): `NaiveBayesAbsenteismo`.
- Serviço de projeção *agregada* de faltas usando somente histórico encerrado (`REALIZADA` / `FALTA`) dos últimos 180 dias.
- Endpoint `GET /dashboard/risco-faltas?ubsId=1` restrito a GESTOR/ADMIN.
- Nova tela `/gestor/risco-faltas` com resumo e projeções diárias.
- Testes unitários do modelo.
- Consultas SQL PostgreSQL em `docs/banco/consultas-avancadas-fase-5.sql`, com agregação, CTE, subconsulta, `DENSE_RANK`, `LAG` e planos de execução.
- Avaliação de árvores B-tree e índices compostos para busca.

## Demonstração rápida
1. Configure Postgres e variáveis do README principal.
2. Rode backend `./mvnw test` e `./mvnw spring-boot:run`.
3. Rode frontend `npm install && npm run dev`.
4. Entre como gestor (`gestor@hackgov.local` / `123456` no seed).
5. Abra `http://localhost:3000/gestor/risco-faltas`.
6. Abra o dashboard, compare dados históricos com estimativas, e execute o SQL no banco.

## Limites e controles
- Modelo didático, não validado em produção; dados seeded são sintéticos.
- Não utiliza dados pessoais diretamente, mas consulta relações de UBS.
- Não penaliza pacientes, não cancela consultas e não executa encaixes.
- Não foi demonstrada precisão preditiva em coorte independente.
- Ao demonstrar, registre valores reais e anexe prints; não declare métricas simuladas como resultados medidos.
- Indexação depende de seletividade/estatísticas e deve ser verificada com EXPLAIN ANALYZE.
- A interface fica acessível pela rota direta; outros links de menu poderão ser harmonizados em evolução posterior.

## Próxima etapa
Validar o treino com separação temporal, comparar contra baseline, adicionar auditoria de versões e política de retenção, considerar vieses e reavaliar após testes com dados autorizados.
