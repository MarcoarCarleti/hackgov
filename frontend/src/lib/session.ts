export type Role = "CIDADAO" | "GESTOR" | "ADMIN";

export type Session = {
  token: string;
  usuarioId: number;
  nome: string;
  role: Role;
};

const KEY = "hackgov_session";

export function getSession(): Session | null {
  if (typeof window === "undefined") return null;
  const raw = localStorage.getItem(KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as Session;
  } catch {
    return null;
  }
}

export function setSession(session: Session) {
  localStorage.setItem(KEY, JSON.stringify(session));
}

export function clearSession() {
  localStorage.removeItem(KEY);
}
