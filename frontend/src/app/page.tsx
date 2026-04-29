"use client";

import Link from "next/link";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { getSession } from "@/lib/session";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    const session = getSession();
    if (!session) return;

    if (session.role === "PACIENTE") {
      router.replace("/cidadao");
      return;
    }

    router.replace("/gestor/dashboard");
  }, [router]);

  return (
    <div className="flex min-h-screen items-center justify-center bg-[linear-gradient(120deg,#e0f2fe,#fef3c7,#dcfce7)] px-4">
      <div className="w-full max-w-xl rounded-3xl bg-white p-8 shadow-2xl">
        <p className="text-sm uppercase tracking-[0.2em] text-slate-500">HackGov</p>
        <h1 className="mt-2 text-3xl font-bold">UBS Inteligente</h1>
        <p className="mt-4 text-slate-600">
          Plataforma de agendamento, cancelamento e reaproveitamento automático de vagas para reduzir absenteísmo.
        </p>

        <div className="mt-8 grid gap-3 sm:grid-cols-2">
          <Link href="/login" className="rounded-xl bg-sky-600 px-4 py-3 text-center font-medium text-white hover:bg-sky-700">
            Entrar
          </Link>
          <Link href="/cadastro" className="rounded-xl border border-slate-300 px-4 py-3 text-center font-medium hover:bg-slate-100">
            Criar conta
          </Link>
        </div>
      </div>
    </div>
  );
}
