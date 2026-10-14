-- Dados fictícios para demonstração local. Preserva os registros existentes.
-- Pode ser executado novamente: horários existentes não são duplicados.
\set ON_ERROR_STOP on
BEGIN;
SET LOCAL TIME ZONE 'America/Sao_Paulo';

WITH candidatos AS (
    SELECT p.id AS profissional_id, p.ubs_id, pe.especialidade_id,
           d::date AS dia, h AS indice_hora,
           time '08:00' + h * interval '30 minutes' AS hora,
           ((d::date - current_date + 30) * 7 + p.id * 3 + h) % 20 AS faixa
    FROM profissional p
    JOIN profissional_especialidade pe ON pe.profissional_id = p.id AND pe.principal
    CROSS JOIN generate_series(current_date - 30, current_date + 7, interval '1 day') d
    CROSS JOIN generate_series(0, 3) h
    WHERE p.ativo
      AND extract(isodow FROM d) BETWEEN 1 AND 5
      AND NOT EXISTS (SELECT 1 FROM feriados f WHERE f.data = d::date)
      AND to_char(d, 'MM-DD') <> '10-12'
), preparados AS (
    SELECT c.*, paciente.id AS usuario_id,
           CASE WHEN dia > current_date THEN 'AGENDADA'
                WHEN faixa < 12 THEN 'REALIZADA'
                WHEN faixa < 17 THEN 'FALTA'
                WHEN faixa < 19 THEN 'CANCELADA_PELO_PACIENTE'
                ELSE 'REALIZADA' END AS status,
           dia <= current_date AND faixa = 19 AS encaixe
    FROM candidatos c
    CROSS JOIN LATERAL (
        SELECT u.id FROM usuario u
        WHERE u.tipo_usuario = 'PACIENTE' AND u.ubs_id = c.ubs_id
        ORDER BY u.id
        OFFSET ((c.profissional_id * 4 + c.indice_hora + (c.dia - current_date + 30)) %
                greatest(1, (SELECT count(*) FROM usuario u2
                             WHERE u2.tipo_usuario = 'PACIENTE' AND u2.ubs_id = c.ubs_id)))
        LIMIT 1
    ) paciente
), novos_horarios AS (
    INSERT INTO agenda_profissional
        (profissional_id, especialidade_id, data_agenda, hora_inicio, hora_fim,
         disponivel, created_at, updated_at)
    SELECT profissional_id, especialidade_id, dia, hora, hora + interval '30 minutes',
           status = 'CANCELADA_PELO_PACIENTE',
           (dia - 10 + time '09:00') AT TIME ZONE 'America/Sao_Paulo', now()
    FROM preparados
    ON CONFLICT (profissional_id, data_agenda, hora_inicio) DO NOTHING
    RETURNING id, profissional_id, data_agenda, hora_inicio
), novas_consultas AS (
    INSERT INTO agendamento
        (usuario_id, agenda_profissional_id, status, observacoes, criado_em,
         cancelado_em, motivo_cancelamento, encaixe_automatico)
    SELECT p.usuario_id, a.id, p.status,
           'Dados fictícios: demonstração do dashboard em outubro de 2026',
           (p.dia - 7 + time '10:00') AT TIME ZONE 'America/Sao_Paulo',
           CASE WHEN p.status = 'CANCELADA_PELO_PACIENTE'
                THEN (p.dia - 2 + time '10:00') AT TIME ZONE 'America/Sao_Paulo' END,
           CASE WHEN p.status = 'CANCELADA_PELO_PACIENTE'
                THEN 'Cancelamento antecipado simulado' END,
           p.encaixe
    FROM novos_horarios a
    JOIN preparados p ON p.profissional_id = a.profissional_id
                     AND p.dia = a.data_agenda AND p.hora = a.hora_inicio
    RETURNING status, encaixe_automatico
)
SELECT status, count(*) AS inseridos,
       count(*) FILTER (WHERE encaixe_automatico) AS encaixes
FROM novas_consultas GROUP BY status ORDER BY status;

COMMIT;
