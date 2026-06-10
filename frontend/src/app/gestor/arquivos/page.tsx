"use client";

import { ChangeEvent, FormEvent, useState } from "react";
import AppShell from "@/components/AppShell";
import { listarAgendamentos } from "@/lib/agendamentos";
import {
  baixarArquivoTexto,
  DocumentoUpload,
  enviarDocumentoSimulado,
  gerarCsvConsultas,
  validarDocumento,
} from "@/lib/arquivos";
import { useAuthGuard } from "@/lib/useAuthGuard";

const links = [
  { href: "/gestor/dashboard", label: "Dashboard" },
  { href: "/gestor/consultas", label: "Consultas" },
  { href: "/gestor/fila-espera", label: "Fila de espera" },
  { href: "/gestor/arquivos", label: "Arquivos" },
  { href: "/gestor/medicos-agendas", label: "Médicos e UBS" },
];

export default function GestorArquivosPage() {
  const { session, ready } = useAuthGuard(["GESTOR", "ADMIN"]);
  const [arquivo, setArquivo] = useState<File | null>(null);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [enviando, setEnviando] = useState(false);
  const [exportando, setExportando] = useState(false);
  const [historico, setHistorico] = useState<DocumentoUpload[]>([]);

  function selecionarArquivo(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0] ?? null;
    setArquivo(file);
    setErro("");
    setSucesso("");

    if (file) {
      const erroValidacao = validarDocumento(file);
      if (erroValidacao) {
        setErro(erroValidacao);
      }
    }
  }

  async function enviar(event: FormEvent) {
    event.preventDefault();
    setErro("");
    setSucesso("");

    if (!arquivo) {
      setErro("Selecione um arquivo antes de enviar.");
      return;
    }

    const erroValidacao = validarDocumento(arquivo);
    if (erroValidacao) {
      setErro(erroValidacao);
      return;
    }

    setEnviando(true);
    try {
      const documento = await enviarDocumentoSimulado(arquivo);
      setHistorico((atual) => [documento, ...atual].slice(0, 5));
      setSucesso("Documento validado e enviado com sucesso.");
      setArquivo(null);
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Falha ao enviar documento.");
    } finally {
      setEnviando(false);
    }
  }

  async function exportarConsultas() {
    setErro("");
    setSucesso("");
    setExportando(true);

    try {
      const consultas = await listarAgendamentos();
      const csv = gerarCsvConsultas(consultas);
      baixarArquivoTexto("relatorio-consultas-hackgov.csv", csv);
      setSucesso("Relatório CSV gerado com sucesso.");
    } catch (e) {
      setErro(e instanceof Error ? e.message : "Não foi possível gerar o relatório.");
    } finally {
      setExportando(false);
    }
  }

  if (!ready || !session) return null;

  return (
    <AppShell title="Arquivos e relatórios" session={session} links={links}>
      <section className="grid gap-4 lg:grid-cols-[1fr_0.9fr]">
        <form onSubmit={enviar} className="rounded-2xl bg-white p-6 shadow-sm">
          <h2 className="text-xl font-semibold">Upload simulado de documento</h2>
          <p className="mt-2 text-slate-600">
            Envie documentos operacionais da UBS com validação de formato e tamanho antes da integração real.
          </p>

          <label htmlFor="documento" className="mt-5 block text-sm font-medium text-slate-700">
            Documento
          </label>
          <input
            id="documento"
            type="file"
            accept=".pdf,.png,.jpg,.jpeg,.csv"
            onChange={selecionarArquivo}
            className="mt-2 w-full rounded-xl border border-slate-300 px-4 py-2 text-sm"
          />
          <p className="mt-2 text-xs text-slate-500">Formatos aceitos: PDF, PNG, JPG, JPEG e CSV. Tamanho máximo: 5 MB.</p>

          {arquivo && (
            <div className="mt-4 rounded-xl border border-slate-200 bg-slate-50 p-3 text-sm">
              <p className="font-medium">{arquivo.name}</p>
              <p className="text-slate-600">{Math.ceil(arquivo.size / 1024)} KB</p>
            </div>
          )}

          {erro && <p className="mt-4 rounded-xl bg-rose-50 p-3 text-sm text-rose-700">{erro}</p>}
          {sucesso && <p className="mt-4 rounded-xl bg-emerald-50 p-3 text-sm text-emerald-700">{sucesso}</p>}

          <button
            type="submit"
            disabled={enviando}
            className="mt-5 rounded-xl bg-sky-600 px-4 py-2 font-medium text-white hover:bg-sky-700 disabled:opacity-60"
          >
            {enviando ? "Enviando documento..." : "Enviar documento"}
          </button>
        </form>

        <div className="space-y-4">
          <section className="rounded-2xl bg-white p-6 shadow-sm">
            <h2 className="text-xl font-semibold">Exportação de relatório</h2>
            <p className="mt-2 text-slate-600">
              Gere um CSV das consultas usando o endpoint protegido do gestor.
            </p>
            <button
              type="button"
              onClick={exportarConsultas}
              disabled={exportando}
              className="mt-5 rounded-xl bg-emerald-600 px-4 py-2 font-medium text-white hover:bg-emerald-700 disabled:opacity-60"
            >
              {exportando ? "Gerando relatório..." : "Baixar CSV de consultas"}
            </button>
          </section>

          <section className="rounded-2xl bg-white p-6 shadow-sm">
            <h2 className="text-xl font-semibold">Histórico da sessão</h2>
            <div className="mt-4 grid gap-3">
              {historico.length === 0 && <p className="text-sm text-slate-600">Nenhum documento enviado nesta sessão.</p>}
              {historico.map((doc) => (
                <div key={doc.id} className="rounded-xl border border-slate-200 p-3 text-sm">
                  <p className="font-medium">{doc.nome}</p>
                  <p className="text-slate-600">{doc.tamanhoKb} KB | {doc.status}</p>
                  <p className="text-xs text-slate-500">{new Date(doc.enviadoEm).toLocaleString("pt-BR")}</p>
                </div>
              ))}
            </div>
          </section>
        </div>
      </section>
    </AppShell>
  );
}
