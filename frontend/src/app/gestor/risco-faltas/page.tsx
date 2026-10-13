"use client";

import { useEffect, useState } from "react";
import AppShell from "@/components/AppShell";
import { apiFetch } from "@/lib/api";
import { useAuthGuard } from "@/lib/useAuthGuard";

type ProjecaoDia = {
  dia: string;
  consultas: number;
  riscoMedioPercentual: number;
  faltasEsperadas: number;
};
type ResultadoRisco = {
  status: string;
  amostrasHistoricas: number;
  faltasHistoricas: number;
  consultasFuturas: number;
  projecoes: ProjecaoDia[];
  observacao: string;
};

const links = [
  { href: "/gestor/dashboard", label: "Dashboard" },
  { href: "/gestor/risco-faltas", label: "Risco de faltas (IA)" },
  { href: "/gestor/consultas", label: "Consultas" },
  { href: "/gestor/fila-espera", label: "Fila de espera" },
  { href: "/gestor/arquivos", label: "Arquivos" },
  { href: "/gestor/medicos-agendas", label: "Médicos e UBS" },
];

export default function RiscoFaltasPage() {
  const { session, ready } = useAuthGuard(["GESTOR", "ADMIN"]);
  const [dados, setDados] = useState<ResultadoRisco | null>(null);
  const [erro, setErro] = useState("");
  useEffect(() => {
    if (!ready) return;
    apiFetch<ResultadoRisco>("/dashboard/risco-faltas")
      .then(setDados)
      .catch((e) => setErro(e instanceof Error ? e.message : "Falha ao analisar histórico"));
  }, [ready]);

  if (!ready || !session) return null;
  return (
    <AppShell title="Planejamento de faltas com IA" session={session} links={links}>
      <div className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold">Estimativa agregada de absenteísmo</h2>
        <p className="mt-2 text-sm text-slate-600">
          Modelo Naive Bayes treinado com consultas históricas concluídas,
          considerando UBS, dia da semana e turno. Não identifica nem classifica pessoas.
        </p>
        {!dados && !erro && <p className="mt-4">Analisando histórico...</p>}
        {erro && <p className="mt-4 text-rose-700" role="alert">{erro}</p>}
        {dados && <>
          <div className="mt-5 grid gap-4 sm:grid-cols-3">
            <div className="rounded-xl bg-sky-50 p-4">Histórico: <strong>{dados.amostrasHistoricas}</strong></div>
            <div className="rounded-xl bg-sky-50 p-4">Faltas históricas: <strong>{dados.faltasHistoricas}</strong></div>
            <div className="rounded-xl bg-sky-50 p-4">Agendamentos futuros: <strong>{dados.consultasFuturas}</strong></div>
          </div>
          <p className="mt-4 text-sm text-slate-600">{dados.observacao}</p>
          {dados.status === "OK" && <div className="mt-6 overflow-x-auto">
            <table className="w-full border-collapse text-left text-sm">
              <thead><tr className="border-b bg-slate-50">
                <th className="p-3">Data</th><th className="p-3">Consultas</th>
                <th className="p-3">Risco médio</th><th className="p-3">Faltas esperadas</th>
              </tr></thead>
              <tbody>{dados.projecoes.map(linha => <tr className="border-b" key={linha.dia}>
                <td className="p-3">{linha.dia}</td>
                <td className="p-3">{linha.consultas}</td>
                <td className="p-3">{linha.riscoMedioPercentual.toFixed(2)}%</td>
                <td className="p-3">{linha.faltasEsperadas.toFixed(2)}</td>
              </tr>)}</tbody>
            </table>
          </div>}
        </>}
      </div>
    </AppShell>
  );
}
