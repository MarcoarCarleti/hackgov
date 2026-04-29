import { apiFetch } from "./api";
import { Consulta } from "./types";

export function listarMeusAgendamentos() {
  return apiFetch<Consulta[]>("/me/consultas");
}

export function listarAgendamentos(ubsId?: number) {
  const query = typeof ubsId === "number" ? `?ubsId=${ubsId}` : "";
  return apiFetch<Consulta[]>(`/consultas${query}`);
}

export function criarAgendamento(agendaSlotId: number, observacoes?: string) {
  return apiFetch<Consulta>("/consultas", {
    method: "POST",
    body: JSON.stringify({ agendaSlotId, observacoes }),
  });
}

export function cancelarAgendamento(id: number, motivo?: string) {
  return apiFetch<Consulta>(`/consultas/${id}/cancelar`, {
    method: "POST",
    body: JSON.stringify({ motivo }),
  });
}

export function atualizarStatusAgendamento(id: number, status: string, observacoes?: string) {
  return apiFetch<Consulta>(`/consultas/${id}/status`, {
    method: "PATCH",
    body: JSON.stringify({ status, observacoes }),
  });
}
