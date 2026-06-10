import { Consulta } from "./types";

export type DocumentoUpload = {
  id: string;
  nome: string;
  tipo: string;
  tamanhoKb: number;
  enviadoEm: string;
  status: "VALIDADO" | "REJEITADO";
};

const extensoesPermitidas = [".pdf", ".png", ".jpg", ".jpeg", ".csv"];
const tamanhoMaximoBytes = 5 * 1024 * 1024;

export function validarDocumento(file: File): string | null {
  const nome = file.name.trim();
  const extensao = nome.slice(nome.lastIndexOf(".")).toLowerCase();

  if (!nome || nome.length > 120) {
    return "O nome do arquivo deve ter até 120 caracteres.";
  }

  if (!extensoesPermitidas.includes(extensao)) {
    return "Formato não permitido. Envie PDF, PNG, JPG, JPEG ou CSV.";
  }

  if (file.size > tamanhoMaximoBytes) {
    return "O arquivo deve ter no máximo 5 MB.";
  }

  return null;
}

export function enviarDocumentoSimulado(file: File): Promise<DocumentoUpload> {
  return new Promise((resolve, reject) => {
    window.setTimeout(() => {
      if (file.name.toLowerCase().includes("falha")) {
        reject(new Error("Não foi possível enviar o arquivo. Tente novamente."));
        return;
      }

      resolve({
        id: crypto.randomUUID(),
        nome: file.name,
        tipo: file.type || "application/octet-stream",
        tamanhoKb: Math.ceil(file.size / 1024),
        enviadoEm: new Date().toISOString(),
        status: "VALIDADO",
      });
    }, 900);
  });
}

function csvCell(value: string | number | boolean | null | undefined) {
  const text = String(value ?? "");
  return `"${text.replace(/"/g, '""')}"`;
}

export function gerarCsvConsultas(consultas: Consulta[]) {
  const header = [
    "id",
    "data",
    "hora",
    "paciente",
    "profissional",
    "especialidade",
    "ubs",
    "status",
    "encaixeAutomatico",
  ];

  const rows = consultas.map((consulta) => [
    consulta.id,
    consulta.dataConsulta,
    consulta.horaConsulta,
    consulta.usuarioNome,
    consulta.medicoNome,
    consulta.especialidade,
    consulta.ubsNome,
    consulta.status,
    consulta.encaixeAutomatico,
  ]);

  return [header, ...rows].map((row) => row.map(csvCell).join(",")).join("\n");
}

export function baixarArquivoTexto(nomeArquivo: string, conteudo: string, tipo = "text/csv;charset=utf-8") {
  const blob = new Blob([conteudo], { type: tipo });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = nomeArquivo;
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
}
