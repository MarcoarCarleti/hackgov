"use client";

import Link from "next/link";
import { useEffect, useMemo, useState } from "react";
import AppShell from "@/components/AppShell";
import { apiFetch } from "@/lib/api";
import { Consulta } from "@/lib/types";
import { useAuthGuard } from "@/lib/useAuthGuard";

const links = [
  { href: "/cidadao", label: "Início" },
  { href: "/cidadao/agenda", label: "Agendar consulta" },
  { href: "/cidadao/minhas-consultas", label: "Minhas consultas" },
  { href: "/cidadao/notificacoes", label: "Notificações" },
];

export default function HomeCidadaoPage() {
  const { session, ready } = useAuthGuard(["CIDADAO"]);
  const [consultas, setConsultas] = useState<Consulta[]>([]);

  useEffect(() => {
    if (!ready) return;
    apiFetch<Consulta[]>("/me/consultas").then(setConsultas).catch(() => setConsultas([]));
  }, [ready]);

  const futuras = useMemo(
    () =>
      consultas
        .filter((c) => ["AGENDADA", "ENCAIXADA"].includes(c.status))
        .slice(0, 3),
    [consultas]
  );

  if (!ready || !session) return null;

  return (
    <AppShell title="Área do Cidadão" session={session} links={links}>
      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold">Bem-vindo, {session.nome}</h2>
        <p className="mt-2 text-slate-600">
          Acompanhe seus agendamentos e, se necessário, cancele com antecedência para liberar a vaga.
        </p>

        <div className="mt-5 grid gap-3 sm:grid-cols-3">
          <Link href="/cidadao/agenda" className="rounded-xl bg-sky-600 px-4 py-3 text-center font-medium text-white hover:bg-sky-700">
            Novo agendamento
          </Link>
          <Link href="/cidadao/minhas-consultas" className="rounded-xl border border-slate-300 px-4 py-3 text-center font-medium hover:bg-slate-100">
            Minhas consultas
          </Link>
          <Link href="/cidadao/notificacoes" className="rounded-xl border border-slate-300 px-4 py-3 text-center font-medium hover:bg-slate-100">
            Notificações
          </Link>
        </div>
      </section>

      <section className="mt-6 rounded-2xl bg-white p-6 shadow-sm">
        <h3 className="text-lg font-semibold">Próximas consultas</h3>
        {futuras.length === 0 && <p className="mt-3 text-slate-600">Você não possui consultas futuras no momento.</p>}
        <div className="mt-4 grid gap-3">
          {futuras.map((c) => (
            <div key={c.id} className="rounded-xl border border-slate-200 p-4">
              <p className="font-medium">{c.dataConsulta} às {c.horaConsulta}</p>
              <p className="text-sm text-slate-600">{c.medicoNome} - {c.especialidade}</p>
              <p className="text-sm text-slate-600">{c.ubsNome} | Status: {c.status}</p>
            </div>
          ))}
        </div>
      </section>
    </AppShell>
  );
}
