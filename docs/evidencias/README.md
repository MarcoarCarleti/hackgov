# Evidências de execução — 13/10/2026

Capturas e validações do ambiente local, com dados fictícios e perfil de gestor.

## Prints

- [Tela de IA](risco-faltas-ia.png): histórico de 601 consultas, 209 faltas históricas e 120 agendamentos futuros; cinco linhas de projeção de 14 a 20/10/2026.
- [Dashboard do gestor](dashboard-gestor.png): 504 consultas, 332 realizadas, 125 faltas, 47 cancelamentos e 27 vagas reaproveitadas.

Capturas reais da aplicação local em Google Chrome headless, autenticado pelo formulário como gestor. Nenhuma resposta da API foi simulada.

## Comandos executados

| Comando | Resultado |
| --- | --- |
| `backend/.\mvnw.cmd test` | Código 0, BUILD SUCCESS, 11 testes, 0 falhas, 0 erros, 0 ignorados |
| `frontend/npm run build` | Código 0, compilação e TypeScript aprovados, 18 páginas geradas |

Os caminhos na tabela indicam o diretório de execução. Os comandos foram `.\mvnw.cmd test` e `npm run build`, respectivamente.

- [Resultado dos testes e avisos do terminal](mvn-test.log)
- [Resultado do build](npm-build.log)
- [Erros e avisos do navegador](navegador.log)

Os arquivos dos comandos registram os resultados relevantes, não o log integral. Os relatórios originais por classe estão em `backend/target/surefire-reports`, na raiz do projeto.

## Avisos observados

- Navegador: três avisos de dimensões iniciais negativas dos gráficos Recharts; os gráficos renderizaram. Nenhum erro JavaScript, requisição falha ou resposta HTTP de erro durante a captura.
- Testes: aviso do Spring Security sobre configuração explícita de `AuthenticationProvider`, emitido duas vezes; aviso da JVM sobre compartilhamento de classes. Não impediram os testes.
- Build: sem erros ou avisos emitidos pelo Next.js.

As integrações de controle do navegador/desktop estavam indisponíveis. A captura foi concluída em um navegador de teste local. A indisponibilidade dessas integrações não é erro da aplicação.
