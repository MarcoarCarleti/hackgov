# Roteiro do Vídeo - Até 5 minutos

## 0:00 a 0:30 - Abertura e problema

Apresentar o grupo, o projeto HackGov Sistema Enterprise e o problema de absenteísmo em consultas da Atenção Básica.

Mensagem sugerida:

"O HackGov UBS Inteligente busca reduzir faltas em consultas, facilitar cancelamentos e permitir reaproveitamento automático de vagas pela fila de espera."

## 0:30 a 1:20 - Solução e evolução do projeto

Mostrar slides 2 e 3.

Pontos:

- cadastro e login;
- agenda do cidadão;
- cancelamento de consulta;
- fila de espera;
- dashboard gerencial;
- evolução da Fase 4 com arquivos, documentação, segurança e blockchain.

## 1:20 a 2:00 - Arquitetura, React e API

Mostrar slide 4 e parte do slide 5.

Pontos:

- frontend em Next.js e React;
- backend Spring Boot;
- banco PostgreSQL;
- API REST;
- cliente centralizado em `frontend/src/lib/api.ts`;
- autenticação JWT.

## 2:00 a 2:40 - Segurança, arquivos e blockchain

Mostrar slides 7, 8 e 9.

Pontos:

- validações;
- perfis `PACIENTE`, `GESTOR` e `ADMIN`;
- senha com BCrypt;
- erros seguros;
- upload simulado e CSV;
- blockchain como evidência imutável por hash.

## 2:40 a 4:40 - Demonstração da aplicação

Roteiro de demonstração:

1. Abrir `http://localhost:3000`.
2. Fazer login como gestor:
   - `gestor@hackgov.local`
   - `123456`
3. Mostrar dashboard e KPIs.
4. Acessar "Consultas" e alterar status de uma consulta.
5. Acessar "Fila de espera".
6. Acessar "Arquivos".
7. Enviar arquivo válido pequeno.
8. Tentar arquivo inválido ou arquivo com nome contendo `falha` para mostrar erro.
9. Baixar relatório CSV.
10. Fazer login como cidadão.
11. Mostrar agenda disponível e fluxo de agendamento.

## 4:40 a 5:00 - Conclusão

Mensagem sugerida:

"A Fase 4 entrega um MVP fullstack funcional, com React, API, estados de integração, segurança, manipulação de arquivos, documentação final, slides e roteiro de apresentação. As próximas evoluções são persistência real de arquivos, notificações externas e prova de conceito de blockchain."
