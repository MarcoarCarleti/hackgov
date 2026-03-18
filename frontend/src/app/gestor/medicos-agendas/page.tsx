"use client";

import { useEffect, useState } from "react";
import AppShell from "@/components/AppShell";
import { apiFetch } from "@/lib/api";
import { useAuthGuard } from "@/lib/useAuthGuard";

type Medico = {
  id: number;
  nome: string;
  especialidade: string;
  ubsNome: string;
  ativo: boolean;
};

type Ubs = {
  id: number;
  nome: string;
  endereco: string;
  cidade: string;
  estado: string;
  telefone: string;
};

const links = [
  { href: "/gestor/dashboard", label: "Dashboard" },
  { href: "/gestor/consultas", label: "Consultas" },
  { href: "/gestor/fila-espera", label: "Fila de espera" },
  { href: "/gestor/medicos-agendas", label: "Médicos e UBS" },
];

export default function GestorMedicosAgendasPage() {
  const { session, ready } = useAuthGuard(["GESTOR", "ADMIN"]);
  const [medicos, setMedicos] = useState<Medico[]>([]);
  const [ubs, setUbs] = useState<Ubs[]>([]);

  useEffect(() => {
    if (!ready) return;
    apiFetch<Medico[]>("/medicos").then(setMedicos).catch(() => setMedicos([]));
    apiFetch<Ubs[]>("/ubs").then(setUbs).catch(() => setUbs([]));
  }, [ready]);

  if (!ready || !session) return null;

  return (
    <AppShell title="Médicos e UBS" session={session} links={links}>
      <section className="grid gap-4 lg:grid-cols-2">
        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <h2 className="text-xl font-semibold">Médicos</h2>
          <div className="mt-4 grid gap-3">
            {medicos.map((m) => (
              <div key={m.id} className="rounded-xl border border-slate-200 p-3">
                <p className="font-medium">{m.nome}</p>
                <p className="text-sm text-slate-600">{m.especialidade}</p>
                <p className="text-sm text-slate-600">{m.ubsNome}</p>
                <p className="text-xs text-slate-500">{m.ativo ? "Ativo" : "Inativo"}</p>
              </div>
            ))}
          </div>
        </div>

        <div className="rounded-2xl bg-white p-6 shadow-sm">
          <h2 className="text-xl font-semibold">UBS cadastradas</h2>
          <div className="mt-4 grid gap-3">
            {ubs.map((u) => (
              <div key={u.id} className="rounded-xl border border-slate-200 p-3">
                <p className="font-medium">{u.nome}</p>
                <p className="text-sm text-slate-600">{u.endereco}</p>
                <p className="text-sm text-slate-600">{u.cidade} - {u.estado}</p>
                <p className="text-sm text-slate-600">{u.telefone}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </AppShell>
  );
}
