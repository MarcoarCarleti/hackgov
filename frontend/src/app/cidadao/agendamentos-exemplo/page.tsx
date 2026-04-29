"use client";

import { useEffect, useState } from "react";
import AppShell from "@/components/AppShell";
import { listarMeusAgendamentos } from "@/lib/agendamentos";
import { Consulta } from "@/lib/types";
import { useAuthGuard } from "@/lib/useAuthGuard";

const links = [
  { href: "/cidadao", label: "Início" },
  { href: "/cidadao/agenda", label: "Agendar consulta" },
  { href: "/cidadao/minhas-consultas", label: "Minhas consultas" },
  { href: "/cidadao/notificacoes", label: "Notificações" },
];

export default function AgendamentosExemploPage() {
  const { session, ready } = useAuthGuard(["PACIENTE"]);
  const [agendamentos, setAgendamentos] = useState<Consulta[]>([]);
  const [erro, setErro] = useState("");

  useEffect(() => {
    if (!ready) return;
    listarMeusAgendamentos()
      .then((data) => setAgendamentos(data.slice(0, 5)))
      .catch((e) => setErro(e instanceof Error ? e.message : "Erro ao consultar agendamentos"));
  }, [ready]);

  if (!ready || !session) return null;

  return (
    <AppShell title="Exemplo da API de Agendamentos" session={session} links={links}>
      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold">Consumo simples do endpoint `/me/consultas`</h2>
        <p className="mt-2 text-slate-600">
          Esta página usa o service `src/lib/agendamentos.ts` para buscar agendamentos do paciente autenticado.
        </p>

        {erro && <p className="mt-4 text-sm text-rose-600">{erro}</p>}

        <div className="mt-5 grid gap-3">
          {agendamentos.length === 0 && <p className="text-slate-600">Nenhum agendamento retornado pela API.</p>}
          {agendamentos.map((agendamento) => (
            <div key={agendamento.id} className="rounded-xl border border-slate-200 p-4">
              <p className="font-medium">{agendamento.dataConsulta} às {agendamento.horaConsulta}</p>
              <p className="text-sm text-slate-600">{agendamento.medicoNome} - {agendamento.especialidade}</p>
              <p className="text-sm text-slate-600">Status: {agendamento.status}</p>
            </div>
          ))}
        </div>
      </section>
    </AppShell>
  );
}
