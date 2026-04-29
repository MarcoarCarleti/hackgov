"use client";
/* eslint-disable react-hooks/set-state-in-effect */

import { useCallback, useEffect, useState } from "react";
import AppShell from "@/components/AppShell";
import { criarAgendamento } from "@/lib/agendamentos";
import { apiFetch } from "@/lib/api";
import { AgendaDisponivel } from "@/lib/types";
import { useAuthGuard } from "@/lib/useAuthGuard";

const links = [
  { href: "/cidadao", label: "Início" },
  { href: "/cidadao/agenda", label: "Agendar consulta" },
  { href: "/cidadao/minhas-consultas", label: "Minhas consultas" },
  { href: "/cidadao/notificacoes", label: "Notificações" },
];

export default function AgendaCidadaoPage() {
  const { session, ready } = useAuthGuard(["PACIENTE"]);
  const [agenda, setAgenda] = useState<AgendaDisponivel[]>([]);
  const [erro, setErro] = useState("");
  const [mensagem, setMensagem] = useState("");

  const carregarAgenda = useCallback(async () => {
    setErro("");
    try {
      const data = await apiFetch<AgendaDisponivel[]>("/agenda/disponivel");
      setAgenda(data);
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao carregar agenda");
    }
  }, []);

  useEffect(() => {
    if (!ready) return;
    carregarAgenda();
  }, [ready, carregarAgenda]);

  async function agendar(slotId: number) {
    setErro("");
    setMensagem("");
    try {
      await criarAgendamento(slotId);
      setMensagem("Consulta agendada com sucesso.");
      carregarAgenda();
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Falha ao agendar");
    }
  }

  if (!ready || !session) return null;

  return (
    <AppShell title="Agenda disponível" session={session} links={links}>
      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold">Horários da sua UBS de referência</h2>
        <p className="mt-2 text-slate-600">Selecione um horário livre e confirme o agendamento.</p>

        {erro && <p className="mt-4 text-sm text-rose-600">{erro}</p>}
        {mensagem && <p className="mt-4 text-sm text-emerald-700">{mensagem}</p>}

        <div className="mt-5 grid gap-3">
          {agenda.length === 0 && <p className="text-slate-600">Nenhum horário disponível no período.</p>}
          {agenda.map((slot) => (
            <div key={slot.slotId} className="flex flex-col gap-2 rounded-xl border border-slate-200 p-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="font-medium">{slot.data} às {slot.horaInicio}</p>
                <p className="text-sm text-slate-600">{slot.medicoNome} - {slot.especialidade}</p>
                <p className="text-sm text-slate-600">{slot.ubsNome}</p>
              </div>
              <button
                onClick={() => agendar(slot.slotId)}
                className="rounded-lg bg-sky-600 px-4 py-2 text-white hover:bg-sky-700"
              >
                Confirmar agendamento
              </button>
            </div>
          ))}
        </div>
      </section>
    </AppShell>
  );
}
