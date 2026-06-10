# Slides - HackGov Sistema Enterprise - Fase 4

## Slide 1 - Capa

- HackGov Sistema Enterprise - UBS Inteligente
- Integrantes: [NOME DO INTEGRANTE] - [RM]
- Turma: [TURMA]
- Foto dos integrantes: [INSERIR FOTO]

## Slide 2 - Problema e objetivo

- Problema: faltas em consultas reduzem a capacidade da UBS.
- Impacto: aumento da fila e perda de horários disponíveis.
- Objetivo da Fase 4: completar frontend, integração, segurança, arquivos, blockchain e documentação.

## Slide 3 - Evolução do projeto

- Já existia backend Spring Boot com regras de agendamento.
- Já existia frontend Next.js com telas de cidadão e gestor.
- Nesta fase foram adicionados auditoria, documentos finais, backlog, arquivos e materiais de apresentação.

## Slide 4 - Arquitetura da solução

- Frontend: Next.js, React, TypeScript e Tailwind.
- Backend: Spring Boot, Java 17 e API REST.
- Banco: PostgreSQL.
- Segurança: JWT, BCrypt e perfis.
- Arquivos: upload simulado e exportação CSV.

## Slide 5 - Frontend React

- Login e cadastro.
- Agenda do cidadão.
- Minhas consultas e notificações.
- Dashboard do gestor.
- Consultas, fila de espera, médicos, UBS e arquivos.
- Layout responsivo e componentes reutilizáveis.

## Slide 6 - Integração com APIs

- Cliente central em `frontend/src/lib/api.ts`.
- Agendamentos em `frontend/src/lib/agendamentos.ts`.
- Exportação CSV usa `GET /consultas`.
- Estados exibidos: carregando, sucesso e erro.

## Slide 7 - Segurança da informação

- Validação de entradas no frontend e backend.
- Autenticação JWT.
- Autorização por perfil.
- Senhas com BCrypt.
- Mascaramento de dados sensíveis.
- Erros amigáveis sem stack trace.

## Slide 8 - Manipulação de arquivos

- Tela `/gestor/arquivos`.
- Upload simulado de PDF, PNG, JPG, JPEG e CSV.
- Tamanho máximo de 5 MB.
- Histórico da sessão.
- Exportação de relatório CSV de consultas.

## Slide 9 - Blockchain

- Uso proposto: evidência imutável de eventos críticos.
- Evento crítico: alteração de status da consulta.
- Registrar: hash, timestamp, tipo do evento e identificador não sensível.
- Não registrar: CPF, Cartão SUS, endereço, documentos ou dados de saúde em texto aberto.

## Slide 10 - Demonstração e conclusão

- Demonstrar login como gestor.
- Abrir dashboard.
- Exportar CSV em Arquivos.
- Simular upload validado.
- Demonstrar login como cidadão e agendamento.
- Conclusão: MVP atende Fase 4 e mantém backend funcional.
