"use client";

import { useEffect, useState } from "react";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import AppShell from "@/components/AppShell";
import KpiCard from "@/components/KpiCard";
import { apiFetch } from "@/lib/api";
import {
  DashboardResumo,
  IndicadorDia,
  RankingMedico,
  ReaproveitamentoDia,
  SerieAgendamentoFalta,
} from "@/lib/types";
import { useAuthGuard } from "@/lib/useAuthGuard";

const links = [
  { href: "/gestor/dashboard", label: "Dashboard" },
  { href: "/gestor/consultas", label: "Consultas" },
  { href: "/gestor/fila-espera", label: "Fila de espera" },
  { href: "/gestor/medicos-agendas", label: "Médicos e UBS" },
];

export default function DashboardPage() {
  const { session, ready } = useAuthGuard(["GESTOR", "ADMIN"]);
  const [resumo, setResumo] = useState<DashboardResumo | null>(null);
  const [agendamentosDia, setAgendamentosDia] = useState<IndicadorDia[]>([]);
  const [reaproveitamento, setReaproveitamento] = useState<ReaproveitamentoDia[]>([]);
  const [ranking, setRanking] = useState<RankingMedico[]>([]);
  const [serie, setSerie] = useState<SerieAgendamentoFalta[]>([]);
  const [erro, setErro] = useState("");

  useEffect(() => {
    if (!ready) return;

    Promise.all([
      apiFetch<DashboardResumo>("/dashboard/resumo"),
      apiFetch<IndicadorDia[]>("/dashboard/agendamentos"),
      apiFetch<ReaproveitamentoDia[]>("/dashboard/reaproveitamento"),
      apiFetch<RankingMedico[]>("/dashboard/ranking-medicos"),
      apiFetch<SerieAgendamentoFalta[]>("/dashboard/serie-agendamentos-faltas"),
    ])
      .then(([r, a, rep, rank, s]) => {
        setResumo(r);
        setAgendamentosDia(a);
        setReaproveitamento(rep);
        setRanking(rank.slice(0, 8));
        setSerie(s);
      })
      .catch((e) => setErro(e instanceof Error ? e.message : "Erro ao carregar dashboard"));
  }, [ready]);

  if (!ready || !session) return null;

  return (
    <AppShell title="Dashboard do Gestor" session={session} links={links}>
      {erro && <p className="mb-4 rounded-xl bg-rose-50 p-3 text-sm text-rose-700">{erro}</p>}

      <section className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
        <KpiCard label="Total agendamentos" value={resumo?.totalAgendamentos ?? "-"} />
        <KpiCard label="Consultas realizadas" value={resumo?.totalRealizadas ?? "-"} tone="good" />
        <KpiCard label="Taxa de faltas" value={`${resumo?.taxaFaltas ?? 0}%`} tone="warn" />
        <KpiCard label="Taxa de cancelamentos" value={`${resumo?.taxaCancelamentos ?? 0}%`} />
        <KpiCard label="Faltas" value={resumo?.totalFaltas ?? "-"} tone="warn" />
        <KpiCard label="Cancelamentos" value={resumo?.totalCancelamentos ?? "-"} />
        <KpiCard label="Vagas reaproveitadas" value={resumo?.vagasReaproveitadas ?? "-"} tone="good" />
      </section>

      <section className="mt-6 grid gap-4 lg:grid-cols-2">
        <div className="rounded-2xl bg-white p-4 shadow-sm">
          <h3 className="mb-3 font-semibold">Ocupação por dia</h3>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={agendamentosDia}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="dia" hide />
                <YAxis />
                <Tooltip />
                <Bar dataKey="quantidade" fill="#0284c7" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="rounded-2xl bg-white p-4 shadow-sm">
          <h3 className="mb-3 font-semibold">Série de agendamentos x faltas</h3>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={serie}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="dia" hide />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="agendamentos" stroke="#0284c7" strokeWidth={2} dot={false} />
                <Line type="monotone" dataKey="faltas" stroke="#dc2626" strokeWidth={2} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>
      </section>

      <section className="mt-6 grid gap-4 lg:grid-cols-2">
        <div className="rounded-2xl bg-white p-4 shadow-sm">
          <h3 className="mb-3 font-semibold">Reaproveitamento por dia</h3>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={reaproveitamento}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="dia" hide />
                <YAxis />
                <Tooltip />
                <Bar dataKey="vagasReaproveitadas" fill="#16a34a" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="rounded-2xl bg-white p-4 shadow-sm">
          <h3 className="mb-3 font-semibold">Ranking de médicos</h3>
          <div className="space-y-2">
            {ranking.map((item, idx) => (
              <div key={item.medico} className="flex items-center justify-between rounded-lg border border-slate-200 px-3 py-2">
                <span className="text-sm">#{idx + 1} {item.medico}</span>
                <span className="rounded-full bg-sky-100 px-2 py-1 text-xs font-medium text-sky-800">{item.total}</span>
              </div>
            ))}
            {ranking.length === 0 && <p className="text-sm text-slate-600">Sem dados para exibir.</p>}
          </div>
        </div>
      </section>
    </AppShell>
  );
}
