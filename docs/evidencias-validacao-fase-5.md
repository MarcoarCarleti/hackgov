# Evidências de validação — HackGov Fase 5 (13/10/2026)

Origem: saída de terminal e capturas apresentadas pelo grupo após executar a aplicação e a API localmente; registros sintéticos de demonstração. As imagens estão no pacote de entrega em `Evidencias_Validacao/`.

## Verificações observadas
- Backend: `cd backend && .\mvnw.cmd test`, Java 17, exit code 0, BUILD SUCCESS.
- 11 testes, 0 falhas, 0 erros, 0 ignorados: AuthValidationIntegrationTest (2), ConsultaServiceRulesTest (5), GestorAuthorizationIntegrationTest (2) e NaiveBayesAbsenteismoTest (2).
- Frontend: `cd frontend && npm run build`, Next.js 16.1.7, build/TypeScript concluídos com exit code 0. Rota `/gestor/risco-faltas` gerada.
- Navegação real: login de gestor → dashboard → tela de risco de faltas; HTTP >=400: 0; pageerror: 0; console.error: 0; requestfailed: 0 neste percurso.

## Resultados exibidos
- IA: 601 resultados históricos, dos quais 209 faltas; 120 consultas futuras, cinco dias com 24 consultas cada.
- Projeções diárias de faltas esperadas: 7,78; 8,66; 7,40; 7,34; 7,47 (total aproximado = 38,65).
- Dashboard: 504 agendamentos, 332 realizadas, 125 faltas, 47 cancelamentos e 27 vagas reaproveitadas; 24,8% de faltas, 9,33% de cancelamentos.
- SQL em base auditada anteriormente: 525 agendamentos, 150 faltas, Hash Join retornou 150 linhas e executou em 0,256 ms. Não confundir as bases ou janelas observadas.

## Limitações
- Não é uma avaliação de acurácia do modelo: estimativas usam dados sintéticos, sem conjunto independente.
- O fluxo navegado não equivale a um teste completo de todas as rotas ou das regras de negócio.
- Autorização negativa especificamente no novo endpoint com perfil PACIENTE ainda exige verificação.
- Três avisos do Recharts sobre largura/altura (-1) apareceram durante a montagem dos gráficos, apesar da renderização visível.
- Avisos do Spring Security e da JVM não impediram a execução dos testes.
- Ainda faltam resultados capturados de `DENSE_RANK`, `LAG` e subconsulta para completar a análise empírica de SQL, além do vídeo no YouTube.

## Pull request
https://github.com/MarcoarCarleti/hackgov/pull/1
