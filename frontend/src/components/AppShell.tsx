"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { clearSession, Session } from "@/lib/session";

type AppShellProps = {
  title: string;
  session: Session;
  links: { href: string; label: string }[];
  children: React.ReactNode;
};

export default function AppShell({ title, session, links, children }: AppShellProps) {
  const router = useRouter();

  function logout() {
    clearSession();
    router.replace("/login");
  }

  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_10%_20%,#e8f5ff,transparent_40%),radial-gradient(circle_at_80%_10%,#f7ffe8,transparent_35%),#f8fafc] text-slate-800">
      <header className="border-b border-slate-200/70 bg-white/75 backdrop-blur">
        <div className="mx-auto flex max-w-6xl flex-wrap items-center justify-between gap-3 px-4 py-3">
          <div>
            <p className="text-xs uppercase tracking-[0.2em] text-slate-500">HackGov UBS Inteligente</p>
            <h1 className="text-lg font-semibold">{title}</h1>
          </div>
          <div className="text-right">
            <p className="text-sm font-medium">{session.nome}</p>
            <button
              onClick={logout}
              className="rounded-lg bg-rose-600 px-3 py-1.5 text-sm text-white hover:bg-rose-700"
            >
              Sair
            </button>
          </div>
        </div>
        <nav className="mx-auto flex max-w-6xl flex-wrap gap-2 px-4 pb-3">
          {links.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className="rounded-full border border-slate-300 bg-white px-3 py-1 text-sm hover:bg-slate-100"
            >
              {item.label}
            </Link>
          ))}
        </nav>
      </header>
      <main className="mx-auto max-w-6xl px-4 py-6">{children}</main>
    </div>
  );
}
