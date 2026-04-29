"use client";

import { useEffect, useState } from "react";
import AppShell from "@/components/AppShell";
import { apiFetch } from "@/lib/api";
import { Notificacao } from "@/lib/types";
import { useAuthGuard } from "@/lib/useAuthGuard";

const links = [
  { href: "/cidadao", label: "Início" },
  { href: "/cidadao/agenda", label: "Agendar consulta" },
  { href: "/cidadao/minhas-consultas", label: "Minhas consultas" },
  { href: "/cidadao/notificacoes", label: "Notificações" },
];

export default function NotificacoesPage() {
  const { session, ready } = useAuthGuard(["PACIENTE"]);
  const [notificacoes, setNotificacoes] = useState<Notificacao[]>([]);
  const [erro, setErro] = useState("");

  useEffect(() => {
    if (!ready) return;
    apiFetch<Notificacao[]>("/notificacoes")
      .then(setNotificacoes)
      .catch((e) => setErro(e instanceof Error ? e.message : "Erro ao carregar notificações"));
  }, [ready]);

  if (!ready || !session) return null;

  return (
    <AppShell title="Notificações" session={session} links={links}>
      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold">Histórico de lembretes</h2>
        <p className="mt-2 text-slate-600">Mensagens automáticas 48h e 24h antes da consulta.</p>

        {erro && <p className="mt-4 text-sm text-rose-600">{erro}</p>}

        <div className="mt-5 grid gap-3">
          {notificacoes.length === 0 && <p className="text-slate-600">Sem notificações enviadas.</p>}
          {notificacoes.map((n) => (
            <div key={n.id} className="rounded-xl border border-slate-200 p-4">
              <p className="text-sm text-slate-500">{n.dataEnvio}</p>
              <p className="font-medium">{n.tipo}</p>
              <p className="text-sm text-slate-600">{n.conteudo}</p>
            </div>
          ))}
        </div>
      </section>
    </AppShell>
  );
}
