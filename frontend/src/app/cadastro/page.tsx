"use client";

import Link from "next/link";
import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import { setSession } from "@/lib/session";

type Ubs = {
  id: number;
  nome: string;
};

export default function CadastroPage() {
  const router = useRouter();
  const [ubsList, setUbsList] = useState<Ubs[]>([]);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [loading, setLoading] = useState(false);

  const [nome, setNome] = useState("");
  const [cpf, setCpf] = useState("");
  const [cartaoSus, setCartaoSus] = useState("");
  const [email, setEmail] = useState("");
  const [telefone, setTelefone] = useState("");
  const [senha, setSenha] = useState("");
  const [ubsReferenciaId, setUbsReferenciaId] = useState<number | "">("");

  useEffect(() => {
    apiFetch<Ubs[]>("/ubs", { auth: false })
      .then(setUbsList)
      .catch(() => setErro("Não foi possível carregar UBS."));
  }, []);

  async function onSubmit(event: FormEvent) {
    event.preventDefault();
    setErro("");
    setSucesso("");

    if (!ubsReferenciaId) {
      setErro("Selecione uma UBS de referência.");
      return;
    }

    setLoading(true);
    try {
      const data = await apiFetch<{ token: string; usuarioId: number; nome: string; role: "PACIENTE" }>("/auth/register", {
        method: "POST",
        auth: false,
        body: JSON.stringify({
          nome,
          cpf,
          cartaoSus,
          email,
          telefone,
          senha,
          ubsReferenciaId,
        }),
      });

      setSession(data);
      setSucesso("Cadastro realizado com sucesso!");
      setTimeout(() => router.replace("/cidadao"), 1000);
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Falha no cadastro");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-100 px-4 py-8">
      <div className="w-full max-w-2xl rounded-2xl bg-white p-8 shadow-xl">
        <h1 className="text-2xl font-bold">Cadastro do paciente</h1>
        <p className="mt-2 text-sm text-slate-600">Informe CPF e Cartão SUS para criar seu acesso.</p>

        <form onSubmit={onSubmit} className="mt-6 grid gap-3 sm:grid-cols-2">
          <input required placeholder="Nome completo" value={nome} onChange={(e) => setNome(e.target.value)} className="rounded-xl border border-slate-300 px-4 py-2 sm:col-span-2" />
          <input required placeholder="CPF (11 dígitos)" maxLength={11} value={cpf} onChange={(e) => setCpf(e.target.value.replace(/\D/g, ""))} className="rounded-xl border border-slate-300 px-4 py-2" />
          <input required placeholder="Cartão SUS (15 dígitos)" maxLength={15} value={cartaoSus} onChange={(e) => setCartaoSus(e.target.value.replace(/\D/g, ""))} className="rounded-xl border border-slate-300 px-4 py-2" />
          <input required type="email" placeholder="E-mail" value={email} onChange={(e) => setEmail(e.target.value)} className="rounded-xl border border-slate-300 px-4 py-2" />
          <input required placeholder="Telefone" value={telefone} onChange={(e) => setTelefone(e.target.value)} className="rounded-xl border border-slate-300 px-4 py-2" />
          <input required minLength={6} type="password" placeholder="Senha" value={senha} onChange={(e) => setSenha(e.target.value)} className="rounded-xl border border-slate-300 px-4 py-2" />

          <select
            required
            value={ubsReferenciaId}
            onChange={(e) => setUbsReferenciaId(Number(e.target.value))}
            className="rounded-xl border border-slate-300 px-4 py-2"
          >
            <option value="">Selecione sua UBS</option>
            {ubsList.map((ubs) => (
              <option key={ubs.id} value={ubs.id}>
                {ubs.nome}
              </option>
            ))}
          </select>

          {erro && <p className="text-sm text-rose-600 sm:col-span-2">{erro}</p>}
          {sucesso && <p className="text-sm text-emerald-700 sm:col-span-2">{sucesso}</p>}

          <button
            type="submit"
            disabled={loading}
            className="rounded-xl bg-sky-600 px-4 py-2 font-medium text-white hover:bg-sky-700 disabled:opacity-60 sm:col-span-2"
          >
            {loading ? "Cadastrando..." : "Cadastrar"}
          </button>
        </form>

        <p className="mt-4 text-sm text-slate-600">
          Já possui conta? <Link href="/login" className="font-medium text-sky-700">Entrar</Link>
        </p>
      </div>
    </div>
  );
}
