"use client";
/* eslint-disable react-hooks/set-state-in-effect */

import { useCallback, useEffect, useState } from "react";
import AppShell from "@/components/AppShell";
import { apiFetch } from "@/lib/api";
import { Consulta } from "@/lib/types";
import { useAuthGuard } from "@/lib/useAuthGuard";

const links = [
  { href: "/gestor/dashboard", label: "Dashboard" },
  { href: "/gestor/consultas", label: "Consultas" },
  { href: "/gestor/fila-espera", label: "Fila de espera" },
  { href: "/gestor/medicos-agendas", label: "Médicos e UBS" },
];

const statusOptions = [
  "AGENDADA",
  "CANCELADA_PELO_PACIENTE",
  "CANCELADA_PELO_SISTEMA",
  "REAGENDADA",
  "REALIZADA",
  "FALTA",
  "ENCAIXADA",
];

export default function GestorConsultasPage() {
  const { session, ready } = useAuthGuard(["GESTOR", "ADMIN"]);
  const [consultas, setConsultas] = useState<Consulta[]>([]);
  const [erro, setErro] = useState("");
  const [msg, setMsg] = useState("");

  const carregar = useCallback(async () => {
    setErro("");
    const data = await apiFetch<Consulta[]>("/consultas");
    setConsultas(data);
  }, []);

  useEffect(() => {
    if (!ready) return;
    carregar().catch((e) => setErro(e instanceof Error ? e.message : "Erro ao carregar consultas"));
  }, [ready, carregar]);

  async function atualizarStatus(id: number, status: string) {
    setErro("");
    setMsg("");
    try {
      await apiFetch(`/consultas/${id}/status`, {
        method: "PATCH",
        body: JSON.stringify({ status }),
      });
      setMsg("Status atualizado com sucesso.");
      carregar();
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao atualizar status");
    }
  }

  if (!ready || !session) return null;

  return (
    <AppShell title="Visão de Consultas" session={session} links={links}>
      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold">Consultas, faltas e cancelamentos</h2>
        <p className="mt-2 text-slate-600">Atualize status operacionais quando necessário.</p>

        {erro && <p className="mt-4 text-sm text-rose-600">{erro}</p>}
        {msg && <p className="mt-4 text-sm text-emerald-700">{msg}</p>}

        <div className="mt-5 overflow-x-auto">
          <table className="min-w-full border-collapse text-sm">
            <thead>
              <tr className="border-b border-slate-200 text-left">
                <th className="px-2 py-2">Data/Hora</th>
                <th className="px-2 py-2">Paciente</th>
                <th className="px-2 py-2">Médico</th>
                <th className="px-2 py-2">UBS</th>
                <th className="px-2 py-2">Status</th>
              </tr>
            </thead>
            <tbody>
              {consultas.map((c) => (
                <tr key={c.id} className="border-b border-slate-100">
                  <td className="px-2 py-2">{c.dataConsulta} {c.horaConsulta}</td>
                  <td className="px-2 py-2">{c.usuarioNome}</td>
                  <td className="px-2 py-2">{c.medicoNome}</td>
                  <td className="px-2 py-2">{c.ubsNome}</td>
                  <td className="px-2 py-2">
                    <select
                      value={c.status}
                      onChange={(e) => atualizarStatus(c.id, e.target.value)}
                      className="rounded-lg border border-slate-300 px-2 py-1"
                    >
                      {statusOptions.map((status) => (
                        <option key={status} value={status}>
                          {status}
                        </option>
                      ))}
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </AppShell>
  );
}
