"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import { setSession } from "@/lib/session";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [loading, setLoading] = useState(false);

  async function onSubmit(event: FormEvent) {
    event.preventDefault();
    setErro("");
    setLoading(true);

    try {
      const data = await apiFetch<{ token: string; usuarioId: number; nome: string; role: "PACIENTE" | "GESTOR" | "ADMIN" }>(
        "/auth/login",
        {
          method: "POST",
          body: JSON.stringify({ email, senha }),
          auth: false,
        }
      );

      setSession(data);
      if (data.role === "PACIENTE") {
        router.replace("/cidadao");
        return;
      }
      router.replace("/gestor/dashboard");
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Falha no login");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-100 px-4">
      <div className="w-full max-w-md rounded-2xl bg-white p-8 shadow-xl">
        <h1 className="text-2xl font-bold">Entrar na plataforma</h1>
        <p className="mt-2 text-sm text-slate-600">Use suas credenciais para acessar sua área.</p>

        <form onSubmit={onSubmit} className="mt-6 space-y-4">
          <input
            type="email"
            required
            placeholder="E-mail"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full rounded-xl border border-slate-300 px-4 py-2"
          />
          <input
            type="password"
            required
            placeholder="Senha"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            className="w-full rounded-xl border border-slate-300 px-4 py-2"
          />

          {erro && <p className="text-sm text-rose-600">{erro}</p>}

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-xl bg-sky-600 px-4 py-2 font-medium text-white hover:bg-sky-700 disabled:opacity-60"
          >
            {loading ? "Entrando..." : "Entrar"}
          </button>
        </form>

        <p className="mt-4 text-sm text-slate-600">
          Não tem conta? <Link href="/cadastro" className="font-medium text-sky-700">Cadastre-se</Link>
        </p>
      </div>
    </div>
  );
}
