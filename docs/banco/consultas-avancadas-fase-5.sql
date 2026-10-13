-- FASE 5 | SQL AVANCADO PARA O MODELO REAL DO HACKGOV
-- 1) Taxa de faltas por UBS com janela de ranking.
-- Denominador: somente consultas concluidas (FALTA ou REALIZADA).
WITH desempenho AS (
 SELECT u.id AS ubs_id, u.nome AS ubs, COUNT(*) AS concluidas,
        COUNT(*) FILTER (WHERE a.status='FALTA') AS faltas
 FROM agendamento a
 JOIN agenda_profissional ap ON ap.id=a.agenda_profissional_id
 JOIN profissional p ON p.id=ap.profissional_id
 JOIN ubs u ON u.id=p.ubs_id
 WHERE a.status IN ('REALIZADA','FALTA')
 GROUP BY u.id,u.nome
)
SELECT ubs, concluidas, faltas,
 ROUND(100.0*faltas/NULLIF(concluidas,0),2) AS taxa_faltas_pct,
 DENSE_RANK() OVER (ORDER BY 1.0*faltas/NULLIF(concluidas,0) DESC) AS posicao
FROM desempenho
ORDER BY posicao,ubs;

-- 2) Metricas mensais com serie temporal (window function LAG)
WITH serie AS (
 SELECT date_trunc('month',ap.data_agenda)::date AS mes,
        COUNT(*) FILTER (WHERE a.status='FALTA') AS faltas,
        COUNT(*) FILTER (WHERE a.status='REALIZADA') AS realizadas
 FROM agendamento a
 JOIN agenda_profissional ap ON ap.id=a.agenda_profissional_id
 WHERE a.status IN ('REALIZADA','FALTA')
 GROUP BY 1
), taxas AS (
 SELECT mes, faltas, realizadas,
        ROUND(100.0*faltas/NULLIF(faltas+realizadas,0),2) AS taxa_pct
 FROM serie
)
SELECT *, LAG(taxa_pct) OVER (ORDER BY mes) AS taxa_mes_anterior,
       taxa_pct-LAG(taxa_pct) OVER (ORDER BY mes) AS variacao_pontos_percentuais
FROM taxas ORDER BY mes;

-- 3) Subconsulta: profissionais com mais faltas que a media do conjunto
WITH faltas_medico AS (
 SELECT p.id,p.nome,COUNT(*) FILTER (WHERE a.status='FALTA') AS faltas
 FROM profissional p
 JOIN agenda_profissional ap ON ap.profissional_id=p.id
 JOIN agendamento a ON a.agenda_profissional_id=ap.id
 GROUP BY p.id,p.nome
)
SELECT nome,faltas FROM faltas_medico
WHERE faltas > (SELECT AVG(faltas) FROM faltas_medico)
ORDER BY faltas DESC;

-- 4) Auditoria de planejamento de indices (rodar sobre dados reais)
CREATE INDEX IF NOT EXISTS idx_agendamento_status_agenda ON agendamento (status,agenda_profissional_id);
CREATE INDEX IF NOT EXISTS idx_agenda_data_profissional ON agenda_profissional (data_agenda,profissional_id);
CREATE INDEX IF NOT EXISTS idx_fila_espera_status_ubs_prioridade
 ON fila_espera (status,ubs_id,prioridade,created_at);
EXPLAIN (ANALYZE, BUFFERS)
SELECT a.id,a.status,ap.data_agenda
FROM agendamento a JOIN agenda_profissional ap ON ap.id=a.agenda_profissional_id
WHERE ap.data_agenda >= CURRENT_DATE-INTERVAL '30 days'
AND a.status='FALTA';
-- Resultados numericos e planos reais devem ser capturados apos execucao no PostgreSQL.
