import { getSession } from "./session";

const API_BASE = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export async function apiFetch<T>(
  path: string,
  options: RequestInit & { auth?: boolean } = {}
): Promise<T> {
  const session = getSession();
  const auth = options.auth ?? true;
  const headers = new Headers(options.headers ?? {});
  headers.set("Content-Type", "application/json");
  if (auth && session?.token) {
    headers.set("Authorization", `Bearer ${session.token}`);
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (response.status === 204) {
    return {} as T;
  }

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    const message = data?.message ?? "Erro ao processar a requisição";
    throw new Error(message);
  }

  return data as T;
}
