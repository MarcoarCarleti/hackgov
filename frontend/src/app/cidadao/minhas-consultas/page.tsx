"use client";
/* eslint-disable react-hooks/set-state-in-effect */

import { useCallback, useEffect, useState } from "react";
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

export default function MinhasConsultasPage() {
  const { session, ready } = useAuthGuard(["CIDADAO"]);
  const [consultas, setConsultas] = useState<Consulta[]>([]);
  const [erro, setErro] = useState("");
  const [mensagem, setMensagem] = useState("");

  const carregarConsultas = useCallback(async () => {
    try {
      const data = await apiFetch<Consulta[]>("/me/consultas");
      setConsultas(data);
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Erro ao carregar consultas");
    }
  }, []);

  useEffect(() => {
    if (!ready) return;
    carregarConsultas();
  }, [ready, carregarConsultas]);

  async function cancelar(consulta: Consulta) {
    const confirmar = window.confirm("Deseja realmente cancelar esta consulta?");
    if (!confirmar) return;

    setErro("");
    setMensagem("");
    try {
      await apiFetch(`/consultas/${consulta.id}/cancelar`, {
        method: "POST",
        body: JSON.stringify({ motivo: "Cancelamento solicitado pelo cidadão" }),
      });
      setMensagem("Consulta cancelada e vaga liberada com sucesso.");
      carregarConsultas();
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Falha ao cancelar consulta");
    }
  }

  const agora = new Date();

  if (!ready || !session) return null;

  return (
    <AppShell title="Minhas consultas" session={session} links={links}>
      <section className="rounded-2xl bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold">Histórico e cancelamento</h2>
        <p className="mt-2 text-slate-600">Cancelamento é permitido com antecedência mínima de 12h.</p>

        {erro && <p className="mt-4 text-sm text-rose-600">{erro}</p>}
        {mensagem && <p className="mt-4 text-sm text-emerald-700">{mensagem}</p>}

        <div className="mt-5 grid gap-3">
          {consultas.length === 0 && <p className="text-slate-600">Nenhuma consulta encontrada.</p>}
          {consultas.map((consulta) => {
            const horario = new Date(`${consulta.dataConsulta}T${consulta.horaConsulta}`);
            const podeCancelar = ["AGENDADA", "ENCAIXADA"].includes(consulta.status) && horario.getTime() - agora.getTime() >= 12 * 60 * 60 * 1000;

            return (
              <div key={consulta.id} className="rounded-xl border border-slate-200 p-4">
                <p className="font-medium">{consulta.dataConsulta} às {consulta.horaConsulta}</p>
                <p className="text-sm text-slate-600">{consulta.medicoNome} - {consulta.especialidade}</p>
                <p className="text-sm text-slate-600">Status: {consulta.status}</p>

                {podeCancelar && (
                  <button
                    onClick={() => cancelar(consulta)}
                    className="mt-3 rounded-lg bg-rose-600 px-3 py-2 text-sm text-white hover:bg-rose-700"
                  >
                    Cancelar consulta
                  </button>
                )}
              </div>
            );
          })}
        </div>
      </section>
    </AppShell>
  );
}
