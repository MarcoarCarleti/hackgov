# Blockchain no HackGov

## Como poderia ser usada

Blockchain poderia ser usada no HackGov como camada de evidência imutável para eventos críticos do ciclo de atendimento, sem substituir o banco principal.

A abordagem recomendada é registrar apenas evidências verificáveis:

- hash do evento;
- timestamp;
- identificador interno não sensível;
- tipo de evento;
- versão do algoritmo de hash.

O banco PostgreSQL continuaria armazenando os dados operacionais. A blockchain serviria como prova de integridade e anterioridade.

## Referências reais

- European Commission, European Blockchain Services Infrastructure (EBSI): https://digital-strategy.ec.europa.eu/en/policies/european-blockchain-services-infrastructure
- OECD, Blockchains Unchained: Blockchain Technology and its Use in the Public Sector: https://www.oecd.org/content/dam/oecd/en/publications/reports/2018/06/blockchains-unchained_fcbd568f/3c32c429-en.pdf

Essas referências indicam o uso de blockchain no setor público para rastreabilidade, serviços confiáveis, interoperabilidade e auditoria, sempre considerando limites regulatórios e necessidade real do caso de uso.

## Evento crítico recomendado

O evento mais crítico para registro imutável é a alteração de status de uma consulta por gestor ou administrador.

Exemplos:

- `AGENDADA` para `REALIZADA`
- `AGENDADA` para `FALTA`
- `AGENDADA` para `CANCELADA_PELO_SISTEMA`
- criação de encaixe automático

## Por que esse evento precisa de imutabilidade

A alteração de status impacta indicadores públicos, gestão da fila, reaproveitamento de vagas e qualidade do atendimento. Se o status puder ser alterado sem trilha confiável, a UBS perde rastreabilidade operacional.

Um registro imutável permite provar que determinado evento existia em um momento específico e que seu conteúdo não foi alterado depois.

## O que seria registrado como evidência

Exemplo de payload antes do hash:

```json
{
  "tipoEvento": "ALTERACAO_STATUS_CONSULTA",
  "consultaId": 123,
  "statusAnterior": "AGENDADA",
  "statusNovo": "REALIZADA",
  "usuarioResponsavelId": 8,
  "timestamp": "2026-06-10T10:30:00-03:00"
}
```

Na blockchain seria registrado:

- hash SHA-256 do payload;
- timestamp;
- `consultaId` ou identificador de protocolo não sensível;
- tipo do evento;
- versão do esquema.

## O que não deve ser registrado

Não devem ser gravados diretamente na blockchain:

- CPF;
- Cartão SUS;
- endereço;
- telefone;
- e-mail;
- nome completo quando não for indispensável;
- observações clínicas;
- documentos anexados;
- conteúdo de prontuário;
- senha, token ou segredo técnico.

## Diretriz de privacidade

Dados pessoais sensíveis não devem ser gravados diretamente na blockchain. Como registros em blockchain são difíceis ou impossíveis de apagar, gravar dados pessoais em texto aberto criaria risco de descumprimento de privacidade e LGPD.

A solução correta é manter dados pessoais no banco controlado pelo sistema e registrar na blockchain apenas hashes e metadados mínimos.
