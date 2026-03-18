"use client";

import { useEffect, useState } from "react";
import AppShell from "@/components/AppShell";
import { apiFetch } from "@/lib/api";
import { useAuthGuard } from "@/lib/useAuthGuard";

type Fila = {
  id: number;
  usuarioNome: string;
  ubsNome: string;
  medicoNome?: string;
  dataDesejada: string;
  status: string;
  prioridade: number;
};

const links = [
  { href: "/gestor/dashboard", label: "Dashboard" },
  { href: "/gestor/consultas", label: "Consultas" },
  { href: "/gestor/fila-espera", label: "Fila de espera" },
  { href: "/gestor/medicos-agendas", label: "Médicos e UBS" },
];

export default function GestorFilaPage() {
  const { session, ready } = useAuthGuard(["GESTOR", "ADMIN"]);
  const [filas, setFilas] = useState<Fila[]>([]);
  const [erro, setErro] = useState("");

  useEffect(() => {
    if (!ready) return;
    apiFetch<Fila[]>("/fila-espera")
      .then(setFilas)
      .catch((e) => setErro(e instanceof Error ? e.message : "Erro ao carregar fila"));
  }, [ready]);

  if (!ready || !session) return null;

  return (
    <AppShell title="Fila de Espera" session={session} links={links}>
      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold">Fila ativa para encaixe automático</h2>
        <p className="mt-2 text-slate-600">Prioridade menor = maior urgência.</p>
        {erro && <p className="mt-4 text-sm text-rose-600">{erro}</p>}

        <div className="mt-5 grid gap-3">
          {filas.length === 0 && <p className="text-slate-600">Nenhum paciente na fila.</p>}
          {filas.map((fila) => (
            <div key={fila.id} className="rounded-xl border border-slate-200 p-4">
              <p className="font-medium">{fila.usuarioNome}</p>
              <p className="text-sm text-slate-600">UBS: {fila.ubsNome}</p>
              <p className="text-sm text-slate-600">Médico: {fila.medicoNome ?? "Qualquer"}</p>
              <p className="text-sm text-slate-600">Data desejada: {fila.dataDesejada}</p>
              <p className="text-sm text-slate-600">Prioridade: {fila.prioridade} | Status: {fila.status}</p>
            </div>
          ))}
        </div>
      </section>
    </AppShell>
  );
}
